#!/bin/bash

# generate_reference_management.sh
# Generates enhanced reference management components

echo "Generating reference management components..."

# 1. Enhanced ADReference.kt
cat > domain/core/ad/reference/ADReference.kt << 'EOF'
package org.blackerp.domain.ad.reference

import arrow.core.Either
import arrow.core.right
import org.blackerp.domain.EntityMetadata
import org.blackerp.domain.ad.ADObject
import org.blackerp.domain.ad.reference.value.ReferenceName
import org.blackerp.domain.values.DisplayName
import org.blackerp.domain.values.Description
import org.blackerp.shared.ValidationError
import java.util.UUID

data class ADReference(
    override val metadata: EntityMetadata,
    val id: UUID = UUID.randomUUID(),
    val name: ReferenceName,
    override val displayName: DisplayName,
    override val description: Description?,
    val type: ReferenceType,
    val validationRule: ValidationRule?,
    val isActive: Boolean = true,
    val parentId: UUID? = null,
    val sortOrder: Int = 0,
    val cacheStrategy: CacheStrategy = CacheStrategy.NONE
) : ADObject {
    companion object {
        fun create(params: CreateReferenceParams): Either<ReferenceError, ADReference> =
            ADReference(
                metadata = params.metadata,
                name = params.name,
                displayName = params.displayName,
                description = params.description,
                type = params.type,
                validationRule = params.validationRule,
                parentId = params.parentId,
                sortOrder = params.sortOrder,
                cacheStrategy = params.cacheStrategy
            ).right()
    }
}

sealed interface ReferenceType {
    object List : ReferenceType
    data class Table(
        val tableName: String,
        val keyColumn: String, 
        val displayColumn: String,
        val whereClause: String? = null,
        val orderBy: String? = null
    ) : ReferenceType
    object Search : ReferenceType
    data class Custom(
        val validatorClass: String,
        val config: Map<String, String> = emptyMap()
    ) : ReferenceType
}

data class ValidationRule(
    val expression: String,
    val errorMessage: String,
    val parameters: Map<String, String> = emptyMap()
)

enum class CacheStrategy {
    NONE,
    SESSION,
    APPLICATION,
    TIMED
}

data class CreateReferenceParams(
    val metadata: EntityMetadata,
    val name: ReferenceName,
    val displayName: DisplayName,
    val description: Description?,
    val type: ReferenceType,
    val validationRule: ValidationRule? = null,
    val parentId: UUID? = null,
    val sortOrder: Int = 0,
    val cacheStrategy: CacheStrategy = CacheStrategy.NONE
)

sealed class ReferenceError {
    data class ValidationFailed(val errors: List<ValidationError>) : ReferenceError()
    data class DuplicateReference(val name: String) : ReferenceError()
    data class ReferenceNotFound(val name: String) : ReferenceError()
    data class CircularReference(val path: List<String>) : ReferenceError()
    data class InvalidConfiguration(val message: String) : ReferenceError()
}

data class ReferenceValue<T>(
    val key: T,
    val display: String,
    val additionalData: Map<String, Any> = emptyMap()
)
EOF

# 2. Operations Interface
cat > domain/core/ad/reference/ReferenceOperations.kt << 'EOF'
package org.blackerp.domain.ad.reference

import arrow.core.Either
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface ReferenceOperations {
    suspend fun save(reference: ADReference): Either<ReferenceError, ADReference>
    suspend fun findById(id: UUID): Either<ReferenceError, ADReference?>
    suspend fun findByName(name: String): Either<ReferenceError, ADReference?>
    suspend fun search(query: String, pageSize: Int = 20, page: Int = 0): Flow<ADReference>
    suspend fun getValues(
        referenceId: UUID,
        searchText: String? = null,
        pageSize: Int = 20,
        page: Int = 0
    ): Either<ReferenceError, List<ReferenceValue<*>>>
    suspend fun validateValue(referenceId: UUID, value: Any): Either<ReferenceError, Boolean>
    suspend fun getHierarchy(rootId: UUID? = null): Either<ReferenceError, List<ADReference>>
    suspend fun delete(id: UUID): Either<ReferenceError, Unit>
}

interface ReferenceRepository {
    suspend fun save(reference: ADReference): Either<ReferenceError, ADReference>
    suspend fun findById(id: UUID): Either<ReferenceError, ADReference?>
    suspend fun findByName(name: String): Either<ReferenceError, ADReference?>
    suspend fun search(query: String, pageSize: Int, page: Int): Flow<ADReference>
    suspend fun delete(id: UUID): Either<ReferenceError, Unit>
}

interface ReferenceCache {
    suspend fun get(key: String): Any?
    suspend fun put(key: String, value: Any, ttlSeconds: Long? = null)
    suspend fun remove(key: String)
    suspend fun clear()
}
EOF

# 3. Service Implementation
cat > application/services/ReferenceService.kt << 'EOF'
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
EOF

# 4. Controller Implementation
cat > application/api/controllers/ReferenceController.kt << 'EOF'
package org.blackerp.application.api.controllers

import org.springframework.web.bind.annotation.*
import org.springframework.http.ResponseEntity
import org.blackerp.domain.ad.reference.*
import org.blackerp.application.services.ReferenceService
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import java.util.UUID

@RestController
@RequestMapping("/api/references")
class ReferenceController(
    private val referenceService: ReferenceService
) {
    private val logger = LoggerFactory.getLogger(ReferenceController::class.java)

    @PostMapping
    suspend fun createReference(
        @Valid @RequestBody request: CreateReferenceRequest
    ): ResponseEntity<ADReference> {
        logger.debug("Creating reference: ${request.name}")
        return referenceService.save(request.toDomain()).fold(
            { error -> ResponseEntity.badRequest().build() },
            { reference -> ResponseEntity.ok(reference) }
        )
    }

    @GetMapping("/{id}")
    suspend fun getReference(@PathVariable id: UUID): ResponseEntity<ADReference> {
        return referenceService.findById(id).fold(
            { error -> ResponseEntity.notFound().build() },
            { reference -> 
                reference?.let { ResponseEntity.ok(it) } 
                    ?: ResponseEntity.notFound().build()
            }
        )
    }

    @GetMapping("/{id}/values")
    suspend fun getReferenceValues(
        @PathVariable id: UUID,
        @RequestParam(required = false) search: String?,
        @RequestParam(defaultValue = "20") pageSize: Int,
        @RequestParam(defaultValue = "0") page: Int
    ): ResponseEntity<List<ReferenceValue<*>>> {
        return referenceService.getValues(id, search, pageSize, page).fold(
            { error -> ResponseEntity.badRequest().build() },
            { values -> ResponseEntity.ok(values) }
        )
    }

    @GetMapping("/{id}/hierarchy")
    suspend fun getReferenceHierarchy(
        @PathVariable id: UUID
    ): ResponseEntity<List<ADReference>> {
        return referenceService.getHierarchy(id).fold(
            { error -> ResponseEntity.badRequest().build() },
            { hierarchy -> ResponseEntity.ok(hierarchy) }
        )
    }

    @DeleteMapping("/{id}")
    suspend fun deleteReference(@PathVariable id: UUID): ResponseEntity<Unit> {
        return referenceService.delete(id).fold(
            { error -> ResponseEntity.badRequest().build() },
            { ResponseEntity.noContent().build() }
        )
    }

    data class CreateReferenceRequest(
        val name: String,
        val displayName: String,
        val description: String?,
        val type: ReferenceTypeRequest,
        val validationRule: ValidationRuleRequest?,
        val parentId: UUID?,
        val sortOrder: Int?,
        val cacheStrategy: String?
    ) {
        fun toDomain(): ADReference {
            // Implement conversion to domain object
            TODO("Implement conversion")
        }
    }

    data class ReferenceTypeRequest(
        val type: String,
        val config: Map<String, String>
    )

    data class ValidationRuleRequest(
        val expression: String,
        val errorMessage: String,
        val parameters: Map<String, String>
    )
}
EOF

echo "Reference management components generated successfully!"
