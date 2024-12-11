package org.blackerp.domain.core.security.evaluation

import org.blackerp.domain.core.security.metadata.SecurityRuleDefinition
import org.blackerp.domain.core.security.SecurityContext
import org.blackerp.domain.core.security.SecurityError
import arrow.core.Either

interface SecurityRuleEvaluator {
    suspend fun evaluate(
        rule: SecurityRuleDefinition,
        context: SecurityContext,
        parameters: Map<String, Any> = emptyMap()
    ): Either<SecurityError, Boolean>

    suspend fun validateRule(rule: SecurityRuleDefinition): Either<SecurityError, Unit>
}
