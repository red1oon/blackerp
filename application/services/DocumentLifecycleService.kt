package org.blackerp.application.services

import org.springframework.stereotype.Service
import org.blackerp.domain.core.ad.document.*
import org.blackerp.domain.core.ad.docstatus.AD_DocStatus
import org.blackerp.domain.core.ad.docaction.DocActionRegistry
import org.blackerp.domain.events.DocumentEvent
import org.blackerp.domain.events.EventMetadata
import org.blackerp.domain.events.DomainEventPublisher
import org.blackerp.infrastructure.persistence.repositories.DocumentHistoryRepository
import arrow.core.Either
import arrow.core.left
import arrow.core.right
import java.util.UUID
import java.time.Instant
import org.slf4j.LoggerFactory

@Service
class DocumentLifecycleService(
    private val documentOperations: DocumentOperations,
    private val docActionRegistry: DocActionRegistry,
    private val eventPublisher: DomainEventPublisher,
    private val historyRepository: DocumentHistoryRepository
) {
    private val logger = LoggerFactory.getLogger(DocumentLifecycleService::class.java)

    suspend fun changeStatus(
        id: UUID,
        command: ChangeStatusCommand
    ): Either<DocumentError, Document> {
        logger.debug("Changing document status: $id -> ${command.targetStatus}")

        return documentOperations.findById(id)
            .flatMap { document ->
                if (document == null) {
                    DocumentError.NotFound(id).left()
                } else {
                    validateStatusTransition(document, command.targetStatus)
                        .flatMap { 
                            executeRequiredAction(document, command.targetStatus)
                                .flatMap {
                                    documentOperations.changeStatus(id, command.targetStatus)
                                        .also { result ->
                                            result.fold(
                                                { /* error handled by caller */ },
                                                { updatedDoc -> 
                                                    trackStatusChange(
                                                        document.status,
                                                        updatedDoc.status,
                                                        id,
                                                        command.reason
                                                    )
                                                }
                                            )
                                        }
                                }
                        }
                }
            }
    }

    suspend fun getHistory(
        id: UUID,
        fromDate: Instant? = null,
        toDate: Instant? = null
    ): Either<DocumentError, List<DocumentChange>> =
        documentOperations.findById(id).map { document ->
            if (fromDate != null && toDate != null) {
                historyRepository.getHistoryBetween(id, fromDate, toDate)
            } else {
                historyRepository.getHistory(id)
            }
        }

    private fun trackStatusChange(
        previousStatus: DocumentStatus,
        newStatus: DocumentStatus,
        documentId: UUID,
        reason: String?
    ) {
        // Track in history
        historyRepository.trackChange(
            DocumentChange(
                id = UUID.randomUUID(),
                documentId = documentId,
                changedAt = Instant.now(),
                changedBy = "system", // TODO: Get from security context
                changes = mapOf(
                    "status" to ChangePair(previousStatus, newStatus)
                )
            )
        )

        // Publish event
        eventPublisher.publish(
            DocumentEvent.StatusChanged(
                metadata = EventMetadata(
                    user = "system", // TODO: Get from security context
                    correlationId = UUID.randomUUID().toString()
                ),
                documentId = documentId,
                previousStatus = previousStatus,
                newStatus = newStatus,
                reason = reason
            )
        )
    }

    // ... rest of the existing methods ...
}
