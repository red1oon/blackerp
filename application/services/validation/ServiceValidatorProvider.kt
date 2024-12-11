package org.blackerp.application.services.validation

import org.springframework.stereotype.Component
import org.blackerp.domain.core.ad.metadata.services.MetadataService
import org.blackerp.infrastructure.validation.RuleBasedValidator
import org.blackerp.domain.core.shared.ValidationError
import arrow.core.Either
import arrow.core.right
import arrow.core.left
import org.slf4j.LoggerFactory
import kotlinx.coroutines.flow.firstOrNull

@Component
class ServiceValidatorProvider(
    private val metadataService: MetadataService,
    private val ruleBasedValidator: RuleBasedValidator
) {
    private val logger = LoggerFactory.getLogger(ServiceValidatorProvider::class.java)

    suspend fun <T : Any> validateEntity(entity: T, entityType: String): Either<ValidationError, T> {
        val context = createValidationContext(entity)
        
        return ruleBasedValidator.validate(entityType, context)
            .map { entity }
            .onLeft { error -> 
                logger.error("Validation failed for $entityType: ${error.message}")
            }
    }

    private fun <T> createValidationContext(entity: T): Map<String, Any> =
        entity?.let {
            mapOf(
                "entity" to it,
                "type" to it::class.java.simpleName
            )
        } ?: emptyMap()
}
