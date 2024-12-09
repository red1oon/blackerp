package org.blackerp.domain.core.ad.metadata.services

import org.springframework.stereotype.Service
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.annotation.CacheEvict
import org.blackerp.domain.core.ad.metadata.entities.*
import java.util.UUID

@Service
class MetadataCacheService {
    companion object {
        const val RULES_CACHE = "rules"
        const val VALIDATIONS_CACHE = "validations"
        const val STATUS_CACHE = "status_lines"
    }

    @Cacheable(value = [RULES_CACHE], key = "#entityType")
    suspend fun getCachedRules(entityType: String): List<ADRule> = emptyList()
    
    @CacheEvict(value = [RULES_CACHE], allEntries = true)
    suspend fun evictRulesCache() {}
    
    @Cacheable(value = [VALIDATIONS_CACHE], key = "#entityType")
    suspend fun getCachedValidations(entityType: String): List<ADValidationRule> = emptyList()
    
    @CacheEvict(value = [VALIDATIONS_CACHE], allEntries = true)
    suspend fun evictValidationsCache() {}
}
