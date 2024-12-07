#!/bin/bash
# File: implement_user_repository.sh
# Description: Implements User persistence with Spring Data JPA

set -e  # Exit on error
echo "Implementing User Repository..."

# Create necessary directories
mkdir -p infrastructure/persistence/repositories
mkdir -p infrastructure/persistence/entities
mkdir -p infrastructure/persistence/mappers
mkdir -p domain/core/security/events

# Function to create file if not exists
create_file() {
    if [ ! -f "$1" ]; then
        mkdir -p "$(dirname "$1")"
        echo "Creating $1..."
        cat > "$1"
    else
        echo "File $1 already exists, skipping..."
    fi
}

# Create JPA Entity
create_file "infrastructure/persistence/entities/UserEntity.kt" << 'EOF'
package org.blackerp.infrastructure.persistence.entities

import jakarta.persistence.*
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "ad_user")
class UserEntity(
    @Id
    @Column(name = "id")
    val id: UUID = UUID.randomUUID(),

    @Column(name = "username", unique = true)
    var username: String,

    @Column(name = "email")
    var email: String,

    @Column(name = "password_hash")
    var passwordHash: String,

    @Column(name = "is_active")
    var isActive: Boolean = true,

    @Column(name = "client_id")
    var clientId: UUID,

    @Column(name = "organization_id")
    var organizationId: UUID? = null,

    @Column(name = "last_login")
    var lastLogin: Instant? = null,

    @Column(name = "created_at")
    val createdAt: Instant = Instant.now(),

    @Column(name = "updated_at")
    var updatedAt: Instant = Instant.now(),

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "ad_user_role",
        joinColumns = [JoinColumn(name = "user_id")]
    )
    var roleIds: MutableSet<UUID> = mutableSetOf()
)
EOF

# Create Spring Data JPA Repository
create_file "infrastructure/persistence/repositories/SpringUserRepository.kt" << 'EOF'
package org.blackerp.infrastructure.persistence.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.blackerp.infrastructure.persistence.entities.UserEntity
import java.util.UUID

@Repository
interface SpringUserRepository : JpaRepository<UserEntity, UUID> {
    fun findByUsername(username: String): UserEntity?
    fun existsByUsername(username: String): Boolean
}
EOF

# Create User Repository Implementation
create_file "infrastructure/persistence/repositories/UserRepositoryImpl.kt" << 'EOF'
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
EOF

# Create User Mapper
create_file "infrastructure/persistence/mappers/UserMapper.kt" << 'EOF'
package org.blackerp.infrastructure.persistence.mappers

import org.springframework.stereotype.Component
import org.blackerp.domain.core.security.*
import org.blackerp.infrastructure.persistence.entities.UserEntity
import java.time.Instant

@Component
class UserMapper {
    fun toDomain(entity: UserEntity): User =
        User(
            id = entity.id,
            username = entity.username,
            email = entity.email,
            isActive = entity.isActive,
            roles = emptySet(), // TODO: Implement role mapping
            clientId = entity.clientId,
            organizationId = entity.organizationId,
            lastLogin = entity.lastLogin
        )

    fun toEntity(domain: User): UserEntity =
        UserEntity(
            id = domain.id,
            username = domain.username,
            email = domain.email,
            passwordHash = "", // Set through security service
            isActive = domain.isActive,
            clientId = domain.clientId,
            organizationId = domain.organizationId,
            lastLogin = domain.lastLogin
        )

    fun updateEntity(entity: UserEntity, domain: User): UserEntity {
        entity.username = domain.username
        entity.email = domain.email
        entity.isActive = domain.isActive
        entity.clientId = domain.clientId
        entity.organizationId = domain.organizationId
        entity.updatedAt = Instant.now()
        return entity
    }
}
EOF

# Create User Events
create_file "domain/core/security/events/UserEvent.kt" << 'EOF'
package org.blackerp.domain.events

import org.blackerp.domain.core.security.User

sealed class UserEvent {
    data class UserCreated(val user: User) : UserEvent()
    data class UserUpdated(val user: User) : UserEvent()
    data class UserLoginAttempted(
        val username: String,
        val success: Boolean,
        val failureReason: String?
    ) : UserEvent()
}
EOF

# Add DB migration script
create_file "infrastructure/database/migration/V1__Create_User_Tables.sql" << 'EOF'
CREATE TABLE ad_user (
    id UUID PRIMARY KEY,
    username VARCHAR(100) UNIQUE NOT NULL,
    email VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    client_id UUID NOT NULL,
    organization_id UUID,
    last_login TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE ad_user_role (
    user_id UUID NOT NULL REFERENCES ad_user(id),
    role_id UUID NOT NULL,
    PRIMARY KEY (user_id, role_id)
);

CREATE INDEX idx_user_username ON ad_user(username);
CREATE INDEX idx_user_client ON ad_user(client_id);
CREATE INDEX idx_user_org ON ad_user(organization_id);
EOF

echo "Implementation complete. Please verify the following files were created:"
echo "- infrastructure/persistence/entities/UserEntity.kt"
echo "- infrastructure/persistence/repositories/SpringUserRepository.kt"
echo "- infrastructure/persistence/repositories/UserRepositoryImpl.kt"
echo "- infrastructure/persistence/mappers/UserMapper.kt"
echo "- domain/core/security/events/UserEvent.kt"
echo "- infrastructure/database/migration/V1__Create_User_Tables.sql"

echo "Next steps:"
echo "1. Implement role persistence"
echo "2. Add user event handlers"
echo "3. Implement security audit logging"
echo "4. Add integration tests"


./compile.sh 