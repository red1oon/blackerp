package org.blackerp.application.services.workflow

import org.springframework.stereotype.Service
import org.blackerp.domain.core.workflow.state.WorkflowStateMachine
import org.blackerp.domain.core.workflow.state.StateTransitionError
import org.blackerp.domain.core.security.SecurityContext
import org.blackerp.domain.core.security.DocumentAccessControl
import org.blackerp.application.services.audit.AuditService
import arrow.core.Either
import arrow.core.flatMap
import java.util.UUID
import org.slf4j.LoggerFactory

@Service
class WorkflowService(
    private val stateMachine: WorkflowStateMachine,
    private val accessControl: DocumentAccessControl,
    private val auditService: AuditService
) {
    private val logger = LoggerFactory.getLogger(WorkflowService::class.java)

    suspend fun transitionState(
        context: SecurityContext,
        documentId: UUID,
        currentState: String,
        targetState: String
    ): Either<StateTransitionError, Unit> {
        logger.debug("Attempting state transition for document $documentId: $currentState -> $targetState")

        return accessControl.checkAccess(context, documentId, "WORKFLOW_TRANSITION")
            .mapLeft { error -> 
                StateTransitionError.PermissionDenied(error.toString())
            }
            .flatMap {
                stateMachine.transition(context, currentState, targetState, documentId)
                    .also { result ->
                        result.fold(
                            { error ->
                                logger.error("State transition failed: ${error.message}")
                            },
                            { newState ->
                                auditService.logAction(
                                    context = context,
                                    entityType = "DOCUMENT",
                                    entityId = documentId,
                                    action = "STATE_TRANSITION",
                                    oldValue = currentState,
                                    newValue = targetState,
                                    metadata = mapOf(
                                        "workflow_state" to newState.name,
                                        "transition_type" to "STATE_CHANGE"
                                    )
                                )
                            }
                        )
                    }
            }
            .map { Unit }
    }
}
