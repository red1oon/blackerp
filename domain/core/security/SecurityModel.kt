package org.blackerp.domain.core.security

import java.util.UUID
import java.time.Instant

data class User(
    val id: UUID = UUID.randomUUID(),
    val username: String,
    val email: String,
    val isActive: Boolean = true,
    val roles: Set<Role>,
    val clientId: UUID,
    val organizationId: UUID?,
    val lastLogin: Instant? = null
)

data class Role(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val permissions: Set<Permission>,
    val scope: SecurityScope
)

data class Permission(
    val id: UUID = UUID.randomUUID(),
    val code: String,
    val description: String,
    val type: PermissionType
)

enum class PermissionType {
    READ, WRITE, DELETE, EXECUTE, ADMIN
}

enum class SecurityScope {
    SYSTEM, CLIENT, ORGANIZATION
}

data class SecurityContext(
    val user: User,
    val clientId: UUID,
    val organizationId: UUID?,
    val roles: Set<Role>
) {
    fun hasPermission(permission: String): Boolean =
        roles.any { role -> role.permissions.any { it.code == permission } }
}
