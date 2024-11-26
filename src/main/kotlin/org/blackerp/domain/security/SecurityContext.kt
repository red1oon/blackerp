package org.blackerp.domain.security

import arrow.core.Either
import org.blackerp.domain.error.DomainError
import java.util.UUID

interface SecurityContext {
    suspend fun getCurrentUser(): Either<DomainError, User>
    suspend fun hasPermission(permission: Permission): Either<DomainError, Boolean>
    suspend fun authenticate(credentials: Credentials): Either<DomainError, User>
}

data class User(
    val id: UUID,
    val username: String,
    val roles: Set<Role>,
    val tenantId: UUID?
)

data class Role(
    val id: UUID,
    val name: String,
    val permissions: Set<Permission>
)

data class Permission(
    val id: UUID,
    val name: String,
    val resource: String,
    val action: String
)

sealed interface Credentials {
    data class Basic(val username: String, val password: String) : Credentials
    data class Token(val token: String) : Credentials
}
