package org.blackerp.application.services.security.evaluation

import org.springframework.stereotype.Service
import org.blackerp.domain.core.security.evaluation.*
import org.blackerp.domain.core.security.metadata.*
import org.blackerp.domain.core.security.SecurityContext
import org.blackerp.domain.core.security.SecurityError
import org.blackerp.infrastructure.metadata.expression.ExpressionEngine
import arrow.core.Either
import arrow.core.left
import arrow.core.right
import org.slf4j.LoggerFactory

@Service
class SecurityRuleEvaluationService(
    private val expressionEngine: ExpressionEngine,
    private val securityRuleRepository: SecurityRuleRepository
) : SecurityRuleEvaluator {
    private val logger = LoggerFactory.getLogger(SecurityRuleEvaluationService::class.java)

    override suspend fun evaluate(
        rule: SecurityRuleDefinition,
        context: SecurityContext,
        parameters: Map<String, Any>
    ): Either<SecurityError, Boolean> {
        try {
            // First check if rule is active
            if (!rule.isActive) {
                return false.right()
            }

            // Check role-based access if role is specified
            rule.roleId?.let { roleId ->
                if (!context.hasRole(roleId)) {
                    return SecurityError.PermissionDenied(
                        "User does not have required role for rule: ${rule.id}"
                    ).left()
                }
            }

            // Evaluate rule expression if present
            rule.expression?.let { expr ->
                val evaluationContext = buildEvaluationContext(context, rule, parameters)
                return expressionEngine.evaluate(expr, evaluationContext)
                    .map { result ->
                        when (result) {
                            is ExpressionResult.BooleanResult -> result.value
                            else -> false
                        }
                    }
                    .mapLeft { error ->
                        SecurityError.ValidationFailed(
                            "Rule evaluation failed: ${error.message}"
                        )
                    }
            }

            // If no expression, rule passes by default
            return true.right()
        } catch (e: Exception) {
            logger.error("Error evaluating security rule: ${rule.id}", e)
            return SecurityError.InternalError(
                "Failed to evaluate security rule: ${e.message}",
                e
            ).left()
        }
    }

    override suspend fun validateRule(rule: SecurityRuleDefinition): Either<SecurityError, Unit> {
        return try {
            // Validate rule expression if present
            rule.expression?.let { expr ->
                expressionEngine.validateExpression(expr)
                    .mapLeft { error ->
                        SecurityError.ValidationFailed(
                            "Invalid rule expression: ${error.message}"
                        )
                    }
            } ?: Unit.right()
        } catch (e: Exception) {
            logger.error("Error validating security rule: ${rule.id}", e)
            SecurityError.InternalError(
                "Failed to validate security rule: ${e.message}",
                e
            ).left()
        }
    }

    private fun buildEvaluationContext(
        context: SecurityContext,
        rule: SecurityRuleDefinition,
        parameters: Map<String, Any>
    ): Map<String, Any> = mapOf(
        "user" to context.user,
        "roles" to context.roles,
        "clientId" to context.clientId,
        "organizationId" to context.organizationId,
        "entityType" to rule.entityType,
        "entityId" to rule.entityId
    ) + parameters
}
