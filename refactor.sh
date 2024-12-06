# document_process_integration.sh
#!/bin/bash

# Create document process integration components
cat > domain/core/ad/document/DocumentProcessHandler.kt << 'EOF'
package org.blackerp.domain.core.ad.document

import org.blackerp.domain.core.ad.process.*
import arrow.core.Either
import arrow.core.left
import arrow.core.right
import org.slf4j.LoggerFactory
import java.util.UUID

interface DocumentProcessHandler {
    suspend fun executeDocumentProcess(
        document: Document,
        processId: UUID,
        parameters: Map<String, Any>
    ): Either<DocumentError, ProcessResult>
}

class DocumentProcessHandlerImpl(
    private val processOperations: ProcessOperations
) : DocumentProcessHandler {
    private val logger = LoggerFactory.getLogger(DocumentProcessHandlerImpl::class.java)

    override suspend fun executeDocumentProcess(
        document: Document,
        processId: UUID,
        parameters: Map<String, Any>
    ): Either<DocumentError, ProcessResult> {
        logger.debug("Executing process $processId for document ${document.id}")

        val documentParameters = createDocumentParameters(document)
        val mergedParameters = parameters + documentParameters

        return processOperations.execute(processId, mergedParameters)
            .mapLeft { error ->
                DocumentError.ValidationFailed("Process execution failed: ${error.message}")
            }
    }

    private fun createDocumentParameters(document: Document): Map<String, Any> = mapOf(
        "DocumentId" to document.id,
        "DocumentType" to document.type.id,
        "DocumentStatus" to document.status.name
    )
}
EOF

# Create document process service integration
cat > application/services/DocumentProcessService.kt << 'EOF'
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
EOF

# Update document events to include process execution
cat > domain/core/ad/document/DocumentEvents.kt << 'EOF'
package org.blackerp.domain.core.ad.document

import org.blackerp.domain.events.DomainEvent
import org.blackerp.domain.events.EventMetadata
import java.util.UUID

sealed class DocumentEvent : DomainEvent {
    data class ProcessExecuted(
        override val metadata: EventMetadata,
        val documentId: UUID,
        val processId: UUID,
        val success: Boolean,
        val message: String
    ) : DocumentEvent()

    // ... existing event classes ...
}
EOF

echo "Document process integration created successfully"

./compile.sh 