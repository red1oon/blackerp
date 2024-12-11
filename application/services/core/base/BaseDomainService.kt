package org.blackerp.application.services.core.base

import arrow.core.Either
import kotlinx.coroutines.flow.Flow
import org.blackerp.domain.core.error.DomainError
import org.blackerp.domain.core.DomainEntity
import java.util.UUID

/**
 * Base interface for all domain services following AD-centric principles.
 * All business logic should be driven by AD metadata, not hardcoded.
 */
interface BaseDomainService<T : DomainEntity, E : DomainError> {
    /**
     * Create a new domain entity
     * @param entity Entity to create
     * @return Either error or created entity
     */
    suspend fun create(entity: T): Either<E, T>

    /**
     * Update existing domain entity
     * @param id Entity identifier
     * @param entity Updated entity data
     * @return Either error or updated entity
     */
    suspend fun update(id: UUID, entity: T): Either<E, T>

    /**
     * Find entity by ID
     * @param id Entity identifier
     * @return Either error or optional entity
     */
    suspend fun findById(id: UUID): Either<E, T?>

    /**
     * Delete entity by ID
     * @param id Entity identifier
     * @return Either error or Unit
     */
    suspend fun delete(id: UUID): Either<E, Unit>

    /**
     * Search entities based on criteria
     * @param criteria Search parameters
     * @return Flow of matching entities 
     */
    suspend fun search(criteria: SearchCriteria): Flow<T>
}
