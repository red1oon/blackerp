package org.blackerp.infrastructure.validation

import org.springframework.stereotype.Component
import org.blackerp.domain.core.ad.metadata.services.RuleEvaluator
import org.blackerp.domain.core.ad.metadata.services.MetadataService
import org.blackerp.domain.core.shared.ValidationError
import arrow.core.Either
import arrow.core.right
import arrow.core.left
import kotlinx.coroutines.flow.firstOrNull
import org.slf4j.LoggerFactory

@Component
class RuleBasedValidator(
    private val metadataService: MetadataService,
    private val ruleEvaluator: RuleEvaluator
) {
    private val logger = LoggerFactory.getLogger(RuleBasedValidator::class.java)

    suspend fun validate(entityType: String, context: Map<String, Any>): Either<ValidationError, Unit> {
        var hasErrors = false
        val errors = mutableListOf<String>()

        metadataService.getRules(entityType).collect { rule ->
            ruleEvaluator.evaluate(rule, context).fold({
                error ->
                    hasErrors = true
                    errors.add(error.message)
            }, { valid ->
                if (!valid) {
                    hasErrors = true
                    errors.add(rule.errorMessage ?: "Validation failed")
                }
            })
        }

        return if (hasErrors) {
            ValidationError.InvalidValue(errors.joinToString(";")).left()
        } else {
            Unit.right()
        }
    }
}
