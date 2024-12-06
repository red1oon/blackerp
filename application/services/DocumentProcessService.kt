package org.blackerp.application.services

import org.springframework.stereotype.Service
import org.blackerp.domain.core.ad.document.*
import org.blackerp.domain.core.ad.process.*
import org.blackerp.domain.events.DocumentEvent
import org.blackerp.domain.events.EventMetadata
import org.blackerp.domain.events.DomainEventPublisher
import arrow.core.Either
import arrow.core.left
import arrow.core.right
import java.util.UUID
import org.slf4j.LoggerFactory

@Service
class DocumentProcessService(
    private val documentOperations: DocumentOperations,
    private val processHandler: DocumentProcessHandler,
    private val eventPublisher: DomainEventPublisher
) {
    private val logger = LoggerFactory.getLogger(DocumentProcessService::class.java)

    suspend fun executeDocumentProcess(
        documentId: UUID,
        processId: UUID,
        parameters: Map<String, Any>
    ): Either<DocumentError, ProcessResult> {
        logger.debug("Executing process $processId for document $documentId")

        return documentOperations.findById(documentId)
            .flatMap { document ->
                if (document == null) {
                    DocumentError.NotFound(documentId).left()
                } else {
                    processHandler.executeDocumentProcess(document, processId, parameters)
                        .also { result ->
                            result.fold(
                                { error -> 
                                    logger.error("Process execution failed: ${error.message}")
                                },
                                { processResult ->
                                    publishProcessExecutionEvent(document, processId, processResult)
                                }
                            )
                        }
                }
            }
    }

    private fun publishProcessExecutionEvent(
        document: Document,
        processId: UUID,
        result: ProcessResult
    ) {
        eventPublisher.publish(
            DocumentEvent.ProcessExecuted(
                metadata = EventMetadata(
                    user = "system", // TODO: Get from security context
                    correlationId = UUID.randomUUID().toString()
                ),
                documentId = UUID.fromString(document.id),
                processId = processId,
                success = result.success,
                message = result.message
            )
        )
    }
}
