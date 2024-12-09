package org.blackerp.application.services

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.blackerp.domain.core.ad.document.*
import org.blackerp.domain.events.DocumentEvent
import org.blackerp.domain.events.EventMetadata
import org.blackerp.infrastructure.events.publishers.DomainEventPublisher
import org.blackerp.application.workflow.engine.WorkflowEngine // Corrected import
import org.blackerp.domain.core.security.SecurityContext
import arrow.core.Either
import arrow.core.left
import arrow.core.right
import java.util.UUID
import java.time.Instant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory

@Service
class DocumentLifecycleService(
    private val documentOperations: DocumentOperations,
    private val workflowEngine: WorkflowEngine,
    private val eventPublisher: DomainEventPublisher
) {
    private val logger = LoggerFactory.getLogger(DocumentLifecycleService::class.java)

    @Transactional
    suspend fun changeStatus(
        id: UUID, 
        command: ChangeStatusCommand
    ): Either<DocumentError, Document> = withContext(Dispatchers.IO) {
        logger.debug("Changing document status: $id -> ${command.targetStatus}")
        
        documentOperations.findById(id).fold(
            { error -> 
                logger.error("Failed to find document: $id", error)
                Either.Left(error) 
            },
            { document ->
                document?.let { doc ->
                    doc.validateStatusTransition(command.targetStatus)
                        .flatMap { executeStatusChange(doc, command) }
                } ?: DocumentError.NotFound(id).left()
            }
        )
    }

    private suspend fun executeStatusChange(
        document: Document, 
        command: ChangeStatusCommand
    ): Either<DocumentError, Document> = withContext(Dispatchers.IO) {
        try {
            // Execute workflow if configured
            if (document.type.workflowId != null) {
                workflowEngine.processDocument(
                    documentId = UUID.fromString(document.id),
                    action = command.targetStatus.name,
                    parameters = command.attributes
                )
            } else {
                // Direct status change
                documentOperations.changeStatus(
                    UUID.fromString(document.id), 
                    command.targetStatus
                ).also { result ->
                    result.fold(
                        { error -> 
                            logger.error("Status change failed", error)
                        },
                        { updatedDoc -> 
                            publishStatusChangeEvent(
                                previousStatus = document.status,
                                newStatus = updatedDoc.status,
                                documentId = UUID.fromString(document.id),
                                reason = command.reason
                            )
                        }
                    )
                }
            }
        } catch (e: Exception) {
            logger.error("Error during status change", e)
            DocumentError.ValidationFailed("Status change failed: ${e.message}").left()
        }
    }

    private suspend fun publishStatusChangeEvent(
        previousStatus: DocumentStatus,
        newStatus: DocumentStatus, 
        documentId: UUID,
        reason: String?
    ) = withContext(Dispatchers.IO) {
        try {
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
        } catch (e: Exception) {
            logger.error("Failed to publish status change event", e)
        }
    }

    suspend fun getHistory(
        id: UUID,
        fromDate: Instant? = null,
        toDate: Instant? = null
    ): Either<DocumentError, List<DocumentChange>> = withContext(Dispatchers.IO) {
        documentOperations.findById(id).map { document ->
            document?.let {
                documentOperations.search(SearchCriteria(
                    types = listOf(UUID.fromString(document.type.id)),
                    dateRange = fromDate?.let { from ->
                        toDate?.let { to ->
                            DateRange(from, to)
                        }
                    }
                )).toList() 
            } ?: emptyList()
        }
    }
}
