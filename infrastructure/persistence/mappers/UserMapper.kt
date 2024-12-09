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
