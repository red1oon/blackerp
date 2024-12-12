package org.blackerp.infrastructure.metadata.services

import org.springframework.stereotype.Service
import org.blackerp.domain.core.ad.metadata.services.*
import org.blackerp.domain.core.ad.metadata.entities.ADRule
import org.blackerp.domain.core.shared.ValidationError
import arrow.core.Either
import arrow.core.left
import arrow.core.right
import org.slf4j.LoggerFactory

@Service
class DefaultMetadataEvaluator : RuleEvaluator {
    private val logger = LoggerFactory.getLogger(DefaultMetadataEvaluator::class.java)
    
    override suspend fun evaluate(rule: ADRule, context: Map<String, Any>): Either<ValidationError, Boolean> {
        return try {
            // Simple evaluation for POC
            true.right()
        } catch (e: Exception) {
            logger.error("Rule evaluation failed", e)
            ValidationError.InvalidValue("Rule evaluation failed: ${e.message}").left()
        }
    }
    
    override suspend fun validateParameters(rule: ADRule, parameters: Map<String, Any>): Either<ValidationError, Map<String, Any>> {
        return try {
            // Simple validation for POC
            parameters.right()
        } catch (e: Exception) {
            logger.error("Parameter validation failed", e)
            ValidationError.InvalidValue("Parameter validation failed: ${e.message}").left()
        }
    }
}
