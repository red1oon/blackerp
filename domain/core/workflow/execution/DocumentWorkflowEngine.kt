package org.blackerp.domain.core.workflow.execution

import arrow.core.Either
import arrow.core.left
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.blackerp.domain.core.ad.document.*
import org.blackerp.domain.core.security.SecurityContext
import org.blackerp.domain.core.workflow.state.StateTransitionError
import org.blackerp.domain.core.workflow.state.WorkflowState
import org.blackerp.domain.core.workflow.state.WorkflowStateMachine
import org.slf4j.LoggerFactory

class DocumentWorkflowEngine(
        private val stateMachine: WorkflowStateMachine,
        private val documentService: DocumentService
) {
    private val logger = LoggerFactory.getLogger(DocumentWorkflowEngine::class.java)

    suspend fun processDocument(
            context: SecurityContext,
            documentId: UUID,
            action: String,
            parameters: Map<String, Any> = emptyMap()
    ): Either<DocumentError, Document> =
            withContext(Dispatchers.IO) {
                logger.debug("Processing document $documentId with action: $action")

                when (val result = documentService.findById(documentId)) {
                    is Either<DocumentError, Document?> -> {
                        val document =
                                result.fold(
                                        { error ->
                                            return@withContext Either.Left(error)
                                        },
                                        { doc ->
                                            doc
                                                    ?: return@withContext DocumentError.NotFound(
                                                                    documentId
                                                            )
                                                            .left()
                                        }
                                )

                        // Check if document has workflow
                        if (document.type.workflowId != null) {
                            executeWorkflowAction(context, document, action, parameters)
                        } else {
                            executeDirectAction(context, document, action, parameters)
                        }
                    }
                    else -> DocumentError.ValidationFailed("Invalid document result").left()
                }
            }

    private suspend fun executeWorkflowAction(
            context: SecurityContext,
            document: Document,
            action: String,
            parameters: Map<String, Any>
    ): Either<DocumentError, Document> =
            withContext(Dispatchers.IO) {
                // Execute workflow-based state transition
                when (val result =
                                stateMachine.transition(
                                        context = context,
                                        currentStateName = document.status.name,
                                        targetStateName = action,
                                        documentId = UUID.fromString(document.id)
                                )
                ) {
                    is Either<StateTransitionError, WorkflowState> -> {
                        result.fold(
                                { error -> mapWorkflowError(error) },
                                { state ->
                                    updateDocumentStatus(
                                            document,
                                            DocumentStatus.valueOf(state.name),
                                            parameters
                                    )
                                }
                        )
                    }
                    else ->
                            DocumentError.ValidationFailed("Invalid workflow transition result")
                                    .left()
                }
            }

    private suspend fun executeDirectAction(
            context: SecurityContext,
            document: Document,
            action: String,
            parameters: Map<String, Any>
    ): Either<DocumentError, Document> =
            withContext(Dispatchers.IO) {
                val targetStatus = DocumentStatus.valueOf(action)
                if (document.type.validateStatusTransition(document.status.name, targetStatus.name)
                ) {
                    updateDocumentStatus(document, targetStatus, parameters)
                } else {
                    DocumentError.StatusTransitionInvalid(
                                    currentStatus = document.status,
                                    targetStatus = targetStatus
                            )
                            .left()
                }
            }

    private suspend fun updateDocumentStatus(
            document: Document,
            newStatus: DocumentStatus,
            parameters: Map<String, Any>
    ): Either<DocumentError, Document> =
            withContext(Dispatchers.IO) {
                documentService.updateDocument(
                        UUID.fromString(document.id),
                        UpdateDocumentCommand(
                                displayName = null,
                                description = null,
                                attributes = parameters
                        )
                )
            }

    private fun mapWorkflowError(error: StateTransitionError): Either<DocumentError, Document> =
            when (error) {
                is StateTransitionError.InvalidTransition ->
                        DocumentError.ValidationFailed(
                                        "Invalid workflow transition: ${error.message}"
                                )
                                .left()
                is StateTransitionError.PermissionDenied ->
                        DocumentError.ValidationFailed("Permission denied: ${error.message}").left()
                is StateTransitionError.ValidationFailed ->
                        DocumentError.ValidationFailed("Validation failed: ${error.message}").left()
            }
}
