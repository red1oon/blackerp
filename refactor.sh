#!/bin/bash

# generate_document_management.sh
# Generates document management components

echo "Generating document management components..."

# 1. Enhanced Document Model
cat > domain/core/ad/document/Document.kt << 'EOF'
package org.blackerp.domain.ad.document

import org.blackerp.domain.EntityMetadata
import org.blackerp.domain.ad.ADObject
import org.blackerp.domain.values.DisplayName
import org.blackerp.domain.values.Description
import arrow.core.Either
import arrow.core.right
import java.util.UUID
import java.time.Instant

data class Document(
    override val metadata: EntityMetadata,
    val id: UUID = UUID.randomUUID(),
    override val displayName: DisplayName,
    override val description: Description?,
    val type: DocumentType,
    val status: DocumentStatus,
    val lines: List<DocumentLine>,
    val baseDocument: UUID? = null,
    val version: Int = 1,
    val workflow: DocumentWorkflow? = null,
    val accessLevel: AccessLevel = AccessLevel.ORGANIZATION,
    val created: Instant = Instant.now(),
    val lastModified: Instant = Instant.now(),
    val archived: Boolean = false
) : ADObject {
    companion object {
        fun create(params: CreateDocumentParams): Either<DocumentError, Document> =
            Document(
                metadata = params.metadata,
                displayName = params.displayName,
                description = params.description,
                type = params.type,
                status = DocumentStatus.DRAFT,
                lines = params.lines,
                baseDocument = params.baseDocument,
                workflow = params.workflow
            ).right()
    }
}

data class DocumentLine(
    val id: UUID = UUID.randomUUID(),
    val lineNo: Int,
    val attributes: Map<String, Any>,
    val referencedDocument: UUID? = null,
    val status: LineStatus = LineStatus.ACTIVE
)

data class DocumentType(
    val id: UUID,
    val code: String,
    val name: String,
    val baseTable: String,
    val linesTable: String?,
    val workflow: UUID?,
    val numberingPattern: String,
    val attributes: Map<String, AttributeDefinition>
)

data class AttributeDefinition(
    val name: String,
    val type: AttributeType,
    val referenceId: UUID?,
    val mandatory: Boolean = false,
    val defaultValue: Any? = null,
    val validationRule: String? = null
)

enum class AttributeType {
    STRING, NUMBER, DATE, BOOLEAN, REFERENCE, DOCUMENT
}

data class DocumentWorkflow(
    val currentState: String,
    val availableTransitions: List<String>,
    val assignee: String?,
    val dueDate: Instant?
)

enum class DocumentStatus {
    DRAFT, IN_PROGRESS, COMPLETED, CANCELLED, VOID
}

enum class LineStatus {
    ACTIVE, CANCELLED, COMPLETED
}

enum class AccessLevel {
    SYSTEM, CLIENT, ORGANIZATION, CLIENT_ORGANIZATION
}
EOF

# 2. Document Operations
cat > domain/core/ad/document/DocumentOperations.kt << 'EOF'
package org.blackerp.domain.ad.document

import arrow.core.Either
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface DocumentOperations {
    suspend fun create(document: Document): Either<DocumentError, Document>
    suspend fun update(id: UUID, document: Document): Either<DocumentError, Document>
    suspend fun findById(id: UUID): Either<DocumentError, Document?>
    suspend fun search(criteria: SearchCriteria): Flow<Document>
    suspend fun delete(id: UUID): Either<DocumentError, Unit>
    suspend fun changeStatus(id: UUID, status: DocumentStatus): Either<DocumentError, Document>
    suspend fun addLine(id: UUID, line: DocumentLine): Either<DocumentError, Document>
    suspend fun updateLine(id: UUID, lineId: UUID, line: DocumentLine): Either<DocumentError, Document>
    suspend fun deleteLine(id: UUID, lineId: UUID): Either<DocumentError, Document>
    suspend fun validateDocument(document: Document): Either<DocumentError, ValidationResult>
    suspend fun getHistory(id: UUID): Flow<DocumentHistory>
}

data class SearchCriteria(
    val types: List<UUID>? = null,
    val statuses: List<DocumentStatus>? = null,
    val dateRange: DateRange? = null,
    val attributes: Map<String, Any>? = null,
    val pageSize: Int = 20,
    val page: Int = 0
)

data class DateRange(
    val from: Instant,
    val to: Instant
)

data class ValidationResult(
    val isValid: Boolean,
    val errors: Map<String, List<String>> = emptyMap()
)

data class DocumentHistory(
    val timestamp: Instant,
    val user: String,
    val action: String,
    val changes: Map<String, ChangePair>
)
EOF

# 3. Document Service
cat > application/services/DocumentService.kt << 'EOF'
package org.blackerp.application.services

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.blackerp.domain.ad.document.*
import arrow.core.Either
import arrow.core.right
import arrow.core.left
import kotlinx.coroutines.flow.Flow
import org.slf4j.LoggerFactory
import java.util.UUID

@Service
class DocumentService(
    private val documentRepository: DocumentRepository,
    private val documentValidator: DocumentValidator,
    private val metadataService: ADMetadataService,
    private val workflowService: WorkflowService
) : DocumentOperations {
    private val logger = LoggerFactory.getLogger(DocumentService::class.java)

    @Transactional
    override suspend fun create(document: Document): Either<DocumentError, Document> {
        logger.debug("Creating document: ${document.displayName}")
        return validateDocument(document).fold(
            { error -> error.left() },
            { validDocument -> documentRepository.save(validDocument) }
        )
    }

    @Transactional
    override suspend fun update(id: UUID, document: Document): Either<DocumentError, Document> {
        logger.debug("Updating document: $id")
        return findById(id).fold(
            { error -> error.left() },
            { existingDoc ->
                if (existingDoc == null) {
                    DocumentError.NotFound(id).left()
                } else {
                    validateUpdate(existingDoc, document).fold(
                        { error -> error.left() },
                        { validDocument -> documentRepository.save(validDocument) }
                    )
                }
            }
        )
    }

    override suspend fun findById(id: UUID): Either<DocumentError, Document?> {
        logger.debug("Finding document by ID: $id")
        return documentRepository.findById(id)
    }

    override suspend fun search(criteria: SearchCriteria): Flow<Document> {
        logger.debug("Searching documents with criteria: $criteria")
        return documentRepository.search(criteria)
    }

    @Transactional
    override suspend fun delete(id: UUID): Either<DocumentError, Unit> {
        logger.debug("Deleting document: $id")
        return findById(id).fold(
            { error -> error.left() },
            { document ->
                if (document == null) {
                    DocumentError.NotFound(id).left()
                } else {
                    documentRepository.delete(id)
                }
            }
        )
    }

    @Transactional
    override suspend fun changeStatus(
        id: UUID,
        status: DocumentStatus
    ): Either<DocumentError, Document> {
        logger.debug("Changing document status: $id -> $status")
        return findById(id).fold(
            { error -> error.left() },
            { document ->
                if (document == null) {
                    DocumentError.NotFound(id).left()
                } else {
                    validateStatusTransition(document, status).fold(
                        { error -> error.left() },
                        { validDocument -> 
                            documentRepository.save(validDocument.copy(status = status))
                        }
                    )
                }
            }
        )
    }

    private suspend fun validateStatusTransition(
        document: Document,
        newStatus: DocumentStatus
    ): Either<DocumentError, Document> {
        // Implementation for status transition validation
        return document.right()
    }

    private suspend fun validateUpdate(
        existingDoc: Document,
        newDoc: Document
    ): Either<DocumentError, Document> {
        // Implementation for update validation
        return newDoc.right()
    }
}
EOF

# 4. Document Controller
cat > application/api/controllers/DocumentController.kt << 'EOF'
package org.blackerp.application.api.controllers

import org.springframework.web.bind.annotation.*
import org.springframework.http.ResponseEntity
import org.blackerp.domain.ad.document.*
import org.blackerp.application.services.DocumentService
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import java.util.UUID

@RestController
@RequestMapping("/api/documents")
class DocumentController(
    private val documentService: DocumentService
) {
    private val logger = LoggerFactory.getLogger(DocumentController::class.java)

    @PostMapping
    suspend fun createDocument(
        @Valid @RequestBody request: CreateDocumentRequest
    ): ResponseEntity<Document> {
        logger.debug("Creating document: ${request.displayName}")
        return documentService.create(request.toDomain()).fold(
            { error -> ResponseEntity.badRequest().build() },
            { document -> ResponseEntity.ok(document) }
        )
    }

    @PutMapping("/{id}")
    suspend fun updateDocument(
        @PathVariable id: UUID,
        @Valid @RequestBody request: UpdateDocumentRequest
    ): ResponseEntity<Document> {
        logger.debug("Updating document: $id")
        return documentService.update(id, request.toDomain()).fold(
            { error -> ResponseEntity.badRequest().build() },
            { document -> ResponseEntity.ok(document) }
        )
    }

    @PostMapping("/{id}/status")
    suspend fun changeStatus(
        @PathVariable id: UUID,
        @Valid @RequestBody request: ChangeStatusRequest
    ): ResponseEntity<Document> {
        logger.debug("Changing document status: $id -> ${request.status}")
        return documentService.changeStatus(id, request.status).fold(
            { error -> ResponseEntity.badRequest().build() },
            { document -> ResponseEntity.ok(document) }
        )
    }

    @GetMapping("/{id}/history")
    suspend fun getHistory(
        @PathVariable id: UUID
    ): ResponseEntity<List<DocumentHistory>> {
        return documentService.getHistory(id)
            .collect { history -> ResponseEntity.ok(history) }
    }

    data class CreateDocumentRequest(
        val displayName: String,
        val description: String?,
        val type: UUID,
        val lines: List<DocumentLineRequest>,
        val baseDocument: UUID?
    ) {
        fun toDomain(): Document {
            // Implementation for conversion to domain object
            TODO("Implement conversion")
        }
    }

    data class UpdateDocumentRequest(
        val displayName: String?,
        val description: String?,
        val lines: List<DocumentLineRequest>?
    ) {
        fun toDomain(): Document {
            // Implementation for conversion to domain object
            TODO("Implement conversion")
        }
    }

    data class DocumentLineRequest(
        val lineNo: Int,
        val attributes: Map<String, Any>,
        val referencedDocument: UUID?
    )

    data class ChangeStatusRequest(
        val status: DocumentStatus
    )
}
EOF

echo "Document management components generated successfully!" 