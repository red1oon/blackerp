package org.blackerp.application.services

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.blackerp.domain.core.ad.document.*
import org.blackerp.domain.events.DocumentEvent
import org.blackerp.domain.events.EventMetadata
import org.blackerp.domain.events.DomainEventPublisher
import org.blackerp.domain.core.shared.ChangePair
import arrow.core.Either
import java.util.UUID
import java.time.Instant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Service
class DocumentLifecycleService(
    private val documentOperations: DocumentOperations,
    private val docActionRegistry: DocActionRegistry,
    private val eventPublisher: DomainEventPublisher
) {
    @Transactional
    suspend fun changeStatus(
        id: UUID,
        command: ChangeStatusCommand
    ): Either<DocumentError, Document> = withContext(Dispatchers.IO) {
        documentOperations.findById(id).fold(
            { error -> Either.Left(error) },
            { document ->
                document?.let { doc ->
                    doc.validateStatusTransition(command.targetStatus)
                        .flatMap { executeStatusChange(doc, command) }
                } ?: Either.Left(DocumentError.NotFound(id))
            }
        )
    }

    private suspend fun executeStatusChange(
        document: Document,
        command: ChangeStatusCommand
    ): Either<DocumentError, Document> = withContext(Dispatchers.IO) {
        documentOperations.changeStatus(
            UUID.fromString(document.id), 
            command.targetStatus
        ).also { result ->
            result.fold(
                { /* error handled by caller */ },
                { updatedDoc ->
                    trackStatusChange(
                        document.status,
                        updatedDoc.status,
                        UUID.fromString(document.id),
                        command.reason
                    )
                }
            )
        }
    }

    private suspend fun trackStatusChange(
        previousStatus: DocumentStatus,
        newStatus: DocumentStatus,
        documentId: UUID,
        reason: String?
    ) = withContext(Dispatchers.IO) {
        eventPublisher.publish(
            DocumentEvent.StatusChanged(
                metadata = EventMetadata(
                    user = "system",
                    correlationId = UUID.randomUUID().toString()
                ),
                documentId = documentId,
                previousStatus = previousStatus,
                newStatus = newStatus,
                reason = reason
            )
        )
    }

    suspend fun getHistory(
        id: UUID,
        fromDate: Instant? = null,
        toDate: Instant? = null
    ): Either<DocumentError, List<DocumentChange>> = withContext(Dispatchers.IO) {
        documentOperations.findById(id).map { document ->
            listOf() // TODO: Implement history retrieval
        }
    }
}
