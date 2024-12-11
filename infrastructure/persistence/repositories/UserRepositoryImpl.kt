// File: infrastructure/persistence/repositories/UserRepositoryImpl.kt
package org.blackerp.infrastructure.persistence.repositories

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import java.util.UUID
import org.blackerp.domain.core.security.*
import org.blackerp.infrastructure.persistence.entities.UserEntity
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class UserRepositoryImpl(
    private val springUserRepository: SpringUserRepository
) : UserRepository {

    private val logger = LoggerFactory.getLogger(UserRepositoryImpl::class.java)

    @Transactional(readOnly = true)
    override suspend fun findByUsername(username: String): Either<SecurityError, User?> = try {
        springUserRepository.findByUsername(username)
            ?.let { entity -> User(
                id = entity.id,
                username = entity.username,
                email = entity.email,
                isActive = entity.isActive,
                roles = emptySet(),  // TODO: Implement role mapping
                clientId = entity.clientId,
                organizationId = entity.organizationId,
                lastLogin = entity.lastLogin
            ) }
            .right()
    } catch (e: Exception) {
        logger.error("Error finding user by username: $username", e)
        SecurityError.UserNotFound("User not found: $username").left()
    }

    @Transactional(readOnly = true)
    override suspend fun findById(id: UUID): Either<SecurityError, User?> = try {
        springUserRepository
            .findById(id)
            .map { entity -> User(
                id = entity.id,
                username = entity.username,
                email = entity.email,
                isActive = entity.isActive,
                roles = emptySet(),  // TODO: Implement role mapping
                clientId = entity.clientId,
                organizationId = entity.organizationId,
                lastLogin = entity.lastLogin
            ) }
            .orElse(null)
            .right()
    } catch (e: Exception) {
        logger.error("Error finding user by id: $id", e)
        SecurityError.UserNotFound("User not found: $id").left()
    }

    @Transactional
    override suspend fun save(user: User): Either<SecurityError, User> = try {
        val entity = springUserRepository.save(UserEntity(
            id = user.id,
            username = user.username,
            email = user.email,
            passwordHash = "", // Set through security service
            isActive = user.isActive,
            clientId = user.clientId,
            organizationId = user.organizationId,
            lastLogin = user.lastLogin
        )
        )

        User(
            id = entity.id,
            username = entity.username,
            email = entity.email,
            isActive = entity.isActive,
            roles = emptySet(),  // TODO: Implement role mapping
            clientId = entity.clientId,
            organizationId = entity.organizationId,
            lastLogin = entity.lastLogin
        ).right()
    } catch (e: Exception) {
        logger.error("Error saving user: ${user.id}", e)
        SecurityError.ValidationFailed("Failed to save user: ${e.message}").left()
    }
}