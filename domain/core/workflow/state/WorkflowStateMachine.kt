package org.blackerp.domain.core.workflow.state

import org.blackerp.domain.core.security.SecurityContext
import arrow.core.Either
import arrow.core.left
import arrow.core.right
import java.util.UUID

sealed class StateTransitionError {
    data class InvalidTransition(val message: String) : StateTransitionError()
    data class PermissionDenied(val message: String) : StateTransitionError()  
    data class ValidationFailed(val message: String) : StateTransitionError()
}

data class WorkflowState(
    val id: UUID,
    val name: String,
    val allowedTransitions: Set<String>,
    val requiredPermissions: Set<String>,
    val validators: List<StateValidator>
)

interface StateValidator {
    suspend fun validate(
        context: SecurityContext,
        currentState: WorkflowState,
        targetState: WorkflowState,
        documentId: UUID
    ): Either<StateTransitionError, Unit>
}

class WorkflowStateMachine(
    private val states: Map<String, WorkflowState>,
    private val initialState: String
) {
    suspend fun transition(
        context: SecurityContext,
        currentStateName: String, 
        targetStateName: String,
        documentId: UUID
    ): Either<StateTransitionError, WorkflowState> {
        val currentState = states[currentStateName] ?: 
            return StateTransitionError.InvalidTransition("Invalid current state: $currentStateName").left()

        val targetState = states[targetStateName] ?:
            return StateTransitionError.InvalidTransition("Invalid target state: $targetStateName").left()

        // Check if transition is allowed
        if (!currentState.allowedTransitions.contains(targetStateName)) {
            return StateTransitionError.InvalidTransition(
                "Transition from $currentStateName to $targetStateName not allowed"
            ).left()
        }

        // Check permissions
        if (!hasRequiredPermissions(context, targetState.requiredPermissions)) {
            return StateTransitionError.PermissionDenied(
                "Missing required permissions for state $targetStateName"
            ).left()
        }

        // Run validators
        currentState.validators.forEach { validator ->
            validator.validate(context, currentState, targetState, documentId).fold(
                { error -> return error.left() },
                { /* continue validation */ }
            )
        }

        return targetState.right()
    }

    private fun hasRequiredPermissions(context: SecurityContext, requiredPermissions: Set<String>): Boolean =
        requiredPermissions.all { permission -> context.hasPermission(permission) }
}
