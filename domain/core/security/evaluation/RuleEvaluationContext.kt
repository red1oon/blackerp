package org.blackerp.domain.core.security.evaluation

import org.blackerp.domain.core.security.SecurityContext
import java.util.UUID

data class RuleEvaluationContext(
    val securityContext: SecurityContext,
    val entityType: String,
    val entityId: UUID,
    val parameters: Map<String, Any> = emptyMap()
)

sealed class EvaluationResult {
    object Allow : EvaluationResult()
    data class Deny(val reason: String) : EvaluationResult()
    data class Error(val message: String, val cause: Throwable? = null) : EvaluationResult()
}
