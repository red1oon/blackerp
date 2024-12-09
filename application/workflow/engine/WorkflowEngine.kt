package org.blackerp.application.workflow.engine

import org.springframework.stereotype.Component
import org.blackerp.domain.core.workflow.transition.*
import org.blackerp.domain.core.ad.document.*
import org.blackerp.domain.core.security.SecurityContext
import org.blackerp.domain.events.WorkflowEvent
import org.blackerp.domain.events.EventMetadata
import org.blackerp.infrastructure.events.publishers.DomainEventPublisher
import arrow.core.Either
import arrow.core.left
import arrow.core.right
import org.slf4j.LoggerFactory
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.transaction.annotation.Transactional

@Component
class WorkflowEngine(
    private val transitionValidator: TransitionValidator,
    private val documentService: DocumentService,
    private val securityContext: SecurityContext,
    private val eventPublisher: DomainEventPublisher
) {
    private val logger = LoggerFactory.getLogger(WorkflowEngine::class.java)

    @Transactional
    suspend fun processDocument(
        documentId: UUID,
        action: String,
        parameters: Map<String, Any> = emptyMap()
    ): Either<WorkflowError, Document> = withContext(Dispatchers.IO) {
        logger.debug("Processing workflow for document $documentId with action: $action")

        try {
            documentService.findById(documentId)
                .flatMap { document ->
                    document?.let { doc ->
                        executeWorkflow(doc, action, parameters)
                    } ?: WorkflowError.NotFound(documentId).left()
                }
        } catch (e: Exception) {
            logger.error("Workflow execution failed for document $documentId", e)
            WorkflowError.ProcessingError("Workflow execution failed: ${e.message}", e).left()
        }
    }

    private suspend fun executeWorkflow(
        document: Document,
        action: String,
        parameters: Map<String, Any>
    ): Either<WorkflowError, Document> {
        val workflowId = document.type.workflowId 
            ?: return WorkflowError.ValidationError("No workflow defined for document type").left()

        return validateTransition(document.status.name, action)
            .flatMap { transition ->
                // Execute any actions defined for this transition
                executeTransitionActions(transition, document, parameters)
                    .flatMap {
                        // Update document status
                        documentService.changeStatus(
                            UUID.fromString(document.id),
                            DocumentStatus.valueOf(action)
                        ).mapLeft { error ->
                            WorkflowError.ProcessingError(error.message)
                        }
                    }
            }
            .also { result ->
                result.fold(
                    { error -> logger.error("Workflow execution failed: ${error.message}") },
                    { updatedDoc -> publishWorkflowEvent(updatedDoc, action) }
                )
            }
    }

    private suspend fun validateTransition(
        currentState: String,
        targetState: String
    ): Either<WorkflowError, StateTransition> {
        return transitionValidator.validate(currentState, targetState)
            .mapLeft { error ->
                when (error) {
                    is TransitionError.InvalidTransition -> WorkflowError.ValidationError(error.message)
                    is TransitionError.PermissionDenied -> WorkflowError.ValidationError(error.message)
                    else -> WorkflowError.ProcessingError(error.message ?: "Unknown error")
                }
            }
    }

    private suspend fun executeTransitionActions(
        transition: StateTransition,
        document: Document,
        parameters: Map<String, Any>
    ): Either<WorkflowError, Unit> {
        // Execute document actions defined for this transition
        // For now just return success
        return Unit.right()
    }

    private fun publishWorkflowEvent(document: Document, action: String) {
        eventPublisher.publish(
            WorkflowEvent.NodeCreated(
                metadata = EventMetadata(
                    user = securityContext.user.username,
                    correlationId = UUID.randomUUID().toString()
                ),
                node = document.type.workflowId!! // Safe as we checked earlier
            )
        )
    }
}
