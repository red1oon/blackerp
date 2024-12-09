package org.blackerp.domain.core.ad.metadata.repositories

import org.blackerp.domain.core.ad.metadata.entities.*
import org.blackerp.domain.core.ad.metadata.services.MetadataError
import arrow.core.Either
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface MetadataRepository {
    // Base metadata operations
    suspend fun <T : Any> save(entity: T): Either<MetadataError, T>
    suspend fun <T : Any> findById(id: UUID, type: Class<T>): Either<MetadataError, T?>

    // Rule specific operations
    suspend fun findRulesByType(entityType: String): Flow<ADRule>
    suspend fun findRulesByIds(ids: List<UUID>): Flow<ADRule>

    // Validation specific operations
    suspend fun findValidationsByEntity(entityType: String): Flow<ADValidationRule>

    // Status specific operations
    suspend fun findStatusLines(documentType: String): Flow<ADStatusLine>
    suspend fun findStatusTransitions(documentType: String, fromStatus: String): Flow<ADStatusLine>
}
