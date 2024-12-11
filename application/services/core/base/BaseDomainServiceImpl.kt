package org.blackerp.application.services.core.base

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.blackerp.domain.core.DomainEntity
import org.blackerp.domain.core.error.DomainError
import org.blackerp.domain.events.DomainEvent
import org.blackerp.infrastructure.events.publishers.DomainEventPublisher
import org.slf4j.LoggerFactory
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

/**
 * Base implementation for domain services that enforces AD-centric principles
 * and provides common functionality for all services.
 */
abstract class BaseDomainServiceImpl<T : DomainEntity, E : DomainError>(
    private val eventPublisher: DomainEventPublisher
) : BaseDomainService<T, E> {

    private val logger = LoggerFactory.getLogger(javaClass)

    @Transactional
    override suspend fun create(entity: T): Either<E, T> = withContext(Dispatchers.IO) {
        try {
            validateCreate(entity).flatMap { validated ->
                executeCreate(validated).also { result ->
                    result.fold(
                        { error -> logger.error("Failed to create entity: ${error.message}") },
                        { created -> publishEvent(createCreatedEvent(created)) }
                    )
                }
            }
        } catch (e: Exception) {
            logger.error("Unexpected error during create", e)
            handleUnexpectedError(e).left()
        }
    }

    @Transactional
    override suspend fun update(id: UUID, entity: T): Either<E, T> = withContext(Dispatchers.IO) {
        try {
            validateUpdate(id, entity).flatMap { validated ->
                executeUpdate(id, validated).also { result ->
                    result.fold(
                        { error -> logger.error("Failed to update entity: ${error.message}") },
                        { updated -> publishEvent(createUpdatedEvent(updated)) }
                    )
                }
            }
        } catch (e: Exception) {
            logger.error("Unexpected error during update", e)
            handleUnexpectedError(e).left()
        }
    }

    @Transactional(readOnly = true)
    override suspend fun findById(id: UUID): Either<E, T?> = withContext(Dispatchers.IO) {
        try {
            executeFindById(id)
        } catch (e: Exception) {
            logger.error("Unexpected error during findById", e)
            handleUnexpectedError(e).left()
        }
    }

    @Transactional
    override suspend fun delete(id: UUID): Either<E, Unit> = withContext(Dispatchers.IO) {
        try {
            executeDelete(id).also { result ->
                result.fold(
                    { error -> logger.error("Failed to delete entity: ${error.message}") },
                    { publishEvent(createDeletedEvent(id)) }
                )
            }
        } catch (e: Exception) {
            logger.error("Unexpected error during delete", e)
            handleUnexpectedError(e).left()
        }
    }

    // Template methods to be implemented by specific services
    protected abstract suspend fun validateCreate(entity: T): Either<E, T>
    protected abstract suspend fun validateUpdate(id: UUID, entity: T): Either<E, T>
    protected abstract suspend fun executeCreate(entity: T): Either<E, T>
    protected abstract suspend fun executeUpdate(id: UUID, entity: T): Either<E, T>
    protected abstract suspend fun executeFindById(id: UUID): Either<E, T?>
    protected abstract suspend fun executeDelete(id: UUID): Either<E, Unit>
    protected abstract fun handleUnexpectedError(e: Exception): E
    
    // Event creation methods to be implemented by specific services
    protected abstract fun createCreatedEvent(entity: T): DomainEvent
    protected abstract fun createUpdatedEvent(entity: T): DomainEvent
    protected abstract fun createDeletedEvent(id: UUID): DomainEvent

    // Common event publishing
    protected suspend fun publishEvent(event: DomainEvent) {
        try {
            eventPublisher.publish(event)
        } catch (e: Exception) {
            logger.error("Failed to publish event", e)
        }
    }
}
