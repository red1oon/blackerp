package org.blackerp.infrastructure.metadata.cache

import org.springframework.stereotype.Component
import org.blackerp.domain.core.ad.metadata.entities.*
import java.util.UUID

@Component
class MetadataCacheService {
    companion object {
        const val RULES_CACHE = "rules"
        const val VALIDATIONS_CACHE = "validations"
        const val STATUS_CACHE = "status_lines"
    }

    suspend fun getCachedRules(entityType: String): List<ADRule> = emptyList()

    suspend fun evictRulesCache() {}

    suspend fun getCachedValidations(entityType: String): List<ADValidationRule> = emptyList()

    suspend fun evictValidationsCache() {}
}
