package org.blackerp.application.services

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.blackerp.domain.ad.reference.*
import arrow.core.Either
import arrow.core.right
import arrow.core.left
import kotlinx.coroutines.flow.Flow
import org.slf4j.LoggerFactory
import java.util.UUID

@Service
class ReferenceService(
    private val referenceRepository: ReferenceRepository,
    private val referenceCache: ReferenceCache,
    private val metadataService: ADMetadataService
) : ReferenceOperations {
    private val logger = LoggerFactory.getLogger(ReferenceService::class.java)

    @Transactional
    override suspend fun save(reference: ADReference): Either<ReferenceError, ADReference> {
        logger.debug("Saving reference: ${reference.name}")
        return referenceRepository.save(reference).also { result ->
            result.fold(
                { error -> logger.error("Failed to save reference: $error") },
                { saved -> 
                    logger.debug("Successfully saved reference: ${saved.id}")
                    clearCacheForReference(saved.id)
                }
            )
        }
    }

    override suspend fun findById(id: UUID): Either<ReferenceError, ADReference?> {
        logger.debug("Finding reference by ID: $id")
        return referenceRepository.findById(id)
    }

    override suspend fun findByName(name: String): Either<ReferenceError, ADReference?> {
        logger.debug("Finding reference by name: $name")
        return referenceRepository.findByName(name)
    }

    override suspend fun search(query: String, pageSize: Int, page: Int): Flow<ADReference> {
        logger.debug("Searching references with query: $query")
        return referenceRepository.search(query, pageSize, page)
    }

    override suspend fun getValues(
        referenceId: UUID,
        searchText: String?,
        pageSize: Int,
        page: Int
    ): Either<ReferenceError, List<ReferenceValue<*>>> {
        logger.debug("Getting values for reference: $referenceId")
        val reference = findById(referenceId).getOrNull() 
            ?: return ReferenceError.ReferenceNotFound(referenceId.toString()).left()
        
        return when (reference.cacheStrategy) {
            CacheStrategy.NONE -> loadValues(reference, searchText, pageSize, page)
            else -> getCachedValues(reference, searchText, pageSize, page)
        }
    }

    override suspend fun validateValue(
        referenceId: UUID,
        value: Any
    ): Either<ReferenceError, Boolean> {
        logger.debug("Validating value for reference: $referenceId")
        return findById(referenceId).fold(
            { error -> error.left() },
            { reference ->
                reference?.validationRule?.let { rule ->
                    validateValueWithRule(value, rule)
                } ?: true.right()
            }
        )
    }

    override suspend fun getHierarchy(rootId: UUID?): Either<ReferenceError, List<ADReference>> {
        logger.debug("Getting hierarchy for root: $rootId")
        return buildHierarchy(rootId)
    }

    @Transactional
    override suspend fun delete(id: UUID): Either<ReferenceError, Unit> {
        logger.debug("Deleting reference: $id")
        return referenceRepository.delete(id).also {
            clearCacheForReference(id)
        }
    }

    private suspend fun getCachedValues(
        reference: ADReference,
        searchText: String?,
        pageSize: Int,
        page: Int
    ): Either<ReferenceError, List<ReferenceValue<*>>> {
        val cacheKey = buildCacheKey(reference.id, searchText, page)
        val cached = referenceCache.get(cacheKey)
        
        return if (cached != null) {
            @Suppress("UNCHECKED_CAST")
            (cached as List<ReferenceValue<*>>).right()
        } else {
            loadValues(reference, searchText, pageSize, page).also { result ->
                result.fold(
                    { /* Don't cache errors */ },
                    { values -> referenceCache.put(cacheKey, values) }
                )
            }
        }
    }

    private suspend fun loadValues(
        reference: ADReference,
        searchText: String?,
        pageSize: Int,
        page: Int
    ): Either<ReferenceError, List<ReferenceValue<*>>> {
        return when (reference.type) {
            is ReferenceType.List -> emptyList<ReferenceValue<*>>().right() // Implement
            is ReferenceType.Table -> emptyList<ReferenceValue<*>>().right() // Implement
            is ReferenceType.Search -> emptyList<ReferenceValue<*>>().right() // Implement
            is ReferenceType.Custom -> emptyList<ReferenceValue<*>>().right() // Implement
        }
    }

    private fun buildCacheKey(referenceId: UUID, searchText: String?, page: Int): String =
        "ref:$referenceId:${searchText ?: ""}:$page"

    private suspend fun clearCacheForReference(referenceId: UUID) {
        logger.debug("Clearing cache for reference: $referenceId")
        referenceCache.remove("ref:$referenceId:*")
    }

    private suspend fun validateValueWithRule(
        value: Any,
        rule: ValidationRule
    ): Either<ReferenceError, Boolean> {
        // Implement validation logic
        return true.right()
    }

    private suspend fun buildHierarchy(rootId: UUID?): Either<ReferenceError, List<ADReference>> {
        // Implement hierarchy building
        return emptyList<ADReference>().right()
    }
}
