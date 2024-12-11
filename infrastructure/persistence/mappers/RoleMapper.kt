package org.blackerp.infrastructure.persistence.mappers

import org.springframework.stereotype.Component
import org.blackerp.domain.core.security.role.Role
import org.blackerp.infrastructure.persistence.entities.RoleEntity
import org.blackerp.domain.core.shared.EntityMetadata
import org.blackerp.domain.core.shared.AuditInfo
import java.util.UUID

@Component
class RoleMapper {
    fun toDomain(entity: RoleEntity): Role =
        Role(
            id = entity.id.toString(),
            name = entity.name,
            description = entity.description,
            permissions = emptySet(), // Will be loaded separately
            clientId = entity.clientId,
            organizationId = entity.organizationId,
            isActive = entity.isActive,
            metadata = EntityMetadata(
                id = entity.id.toString(),
                audit = AuditInfo(
                    createdBy = "system", // TODO: Get from audit
                    updatedBy = "system"
                )
            )
        )

    fun toEntity(domain: Role): RoleEntity =
        RoleEntity(
            id = UUID.fromString(domain.id),
            name = domain.name,
            description = domain.description,
            clientId = domain.clientId,
            organizationId = domain.organizationId,
            isActive = domain.isActive
        )
}
