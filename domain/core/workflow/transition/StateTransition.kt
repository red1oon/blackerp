package org.blackerp.domain.core.workflow.transition

import org.blackerp.domain.core.shared.EntityMetadata
import java.util.UUID

data class StateTransition(
   val id: UUID = UUID.randomUUID(),
   val fromState: String,
   val toState: String,
   val requiredRole: String? = null,
   val condition: TransitionCondition? = null,
   val priority: Int = 0
)

data class TransitionCondition(
   val expression: String,
   val parameters: Map<String, String> = emptyMap()
)

sealed class TransitionResult {
   data class Success(val newState: String) : TransitionResult()
   data class Failure(val reason: String) : TransitionResult() 
}
