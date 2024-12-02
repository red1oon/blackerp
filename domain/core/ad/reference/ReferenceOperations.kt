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
