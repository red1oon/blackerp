#!/bin/bash

# Update both StateTransition and TransitionValidator to resolve dependencies

cat > "domain/core/workflow/transition/StateTransition.kt" << 'EOF'
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
EOF

cat > "domain/core/workflow/transition/TransitionValidator.kt" << 'EOF'
package org.blackerp.domain.core.workflow.transition

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import org.blackerp.domain.core.security.SecurityContext
import java.util.UUID

sealed class TransitionError(open val message: String) {
   data class InvalidTransition(override val message: String) : TransitionError(message)
   data class PermissionDenied(override val message: String) : TransitionError(message)
   data class ValidationFailed(override val message: String) : TransitionError(message)
}

interface TransitionValidator {
   suspend fun validate(
       fromState: String,
       toState: String, 
       context: Map<String, Any> = emptyMap()
   ): Either<TransitionError, StateTransition>
}

interface TransitionRepository {
   suspend fun findTransitions(fromState: String): List<StateTransition>
}

class DefaultTransitionValidator(
   private val transitionRepository: TransitionRepository,
   private val securityContext: SecurityContext
) : TransitionValidator {

   override suspend fun validate(
       fromState: String,
       toState: String,
       context: Map<String, Any>
   ): Either<TransitionError, StateTransition> {
       val transitions = transitionRepository.findTransitions(fromState)
       
       val transition = transitions.find { it.toState == toState }
           ?: return TransitionError.InvalidTransition(
               "No transition defined from $fromState to $toState"
           ).left()

       transition.requiredRole?.let { role ->
           if (!securityContext.hasPermission(role)) {
               return TransitionError.PermissionDenied(
                   "Missing required role: $role"
               ).left()
           }
       }

       transition.condition?.let { cond ->
           if (!evaluateCondition(cond, context)) {
               return TransitionError.ValidationFailed(
                   "Transition condition not met: ${cond.expression}"
               ).left()
           }
       }

       return transition.right()
   }

   private fun evaluateCondition(
       condition: TransitionCondition,
       context: Map<String, Any>
   ): Boolean {
       // Implement condition evaluation
       return true
   }
}
EOF
./compile.sh