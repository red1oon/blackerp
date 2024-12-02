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
