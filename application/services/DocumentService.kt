package org.blackerp.application.services

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.blackerp.domain.core.ad.document.*
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
