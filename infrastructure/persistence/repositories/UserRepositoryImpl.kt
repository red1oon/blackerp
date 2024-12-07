package org.blackerp.infrastructure.persistence.repositories

import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import org.blackerp.domain.core.security.*
import org.blackerp.domain.events.UserEvent
import org.blackerp.infrastructure.persistence.entities.UserEntity
import org.blackerp.infrastructure.persistence.mappers.UserMapper
import org.blackerp.infrastructure.events.publishers.DomainEventPublisher
import arrow.core.Either
import arrow.core.left
import arrow.core.right
import java.util.UUID
import org.slf4j.LoggerFactory

@Repository
class UserRepositoryImpl(
    private val springUserRepository: SpringUserRepository,
    private val userMapper: UserMapper,
    private val eventPublisher: DomainEventPublisher
) : UserRepository {

    private val logger = LoggerFactory.getLogger(UserRepositoryImpl::class.java)

    @Transactional(readOnly = true)
    override suspend fun findByUsername(username: String): Either<SecurityError, User?> =
        try {
            springUserRepository.findByUsername(username)
                ?.let { entity -> userMapper.toDomain(entity).right() }
                ?: null.right()
        } catch (e: Exception) {
            logger.error("Error finding user by username: $username", e)
            SecurityError.UserNotFound("User not found: $username").left()
        }

    @Transactional(readOnly = true)
    override suspend fun findById(id: UUID): Either<SecurityError, User?> =
        try {
            springUserRepository.findById(id)
                .map { entity -> userMapper.toDomain(entity) }
                .orElse(null)
                .right()
        } catch (e: Exception) {
            logger.error("Error finding user by id: $id", e)
            SecurityError.UserNotFound("User not found: $id").left()
        }

    @Transactional
    override suspend fun save(user: User): Either<SecurityError, User> =
        try {
            val existingUser = user.id?.let { springUserRepository.findById(it).orElse(null) }
            val entity = if (existingUser != null) {
                userMapper.updateEntity(existingUser, user)
            } else {
                userMapper.toEntity(user)
            }
            
            val savedEntity = springUserRepository.save(entity)
            val savedUser = userMapper.toDomain(savedEntity)
            
            // Publish event
            val event = if (existingUser == null) {
                UserEvent.UserCreated(savedUser)
            } else {
                UserEvent.UserUpdated(savedUser)
            }
            eventPublisher.publish(event)
            
            savedUser.right()
        } catch (e: Exception) {
            logger.error("Error saving user: ${user.id}", e)
            SecurityError.ValidationFailed("Failed to save user: ${e.message}").left()
        }
}
