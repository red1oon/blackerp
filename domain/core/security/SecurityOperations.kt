package org.blackerp.domain.core.security

import arrow.core.Either
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface SecurityOperations {
    suspend fun authenticate(credentials: Credentials): Either<SecurityError, AuthToken>
    suspend fun validateToken(token: String): Either<SecurityError, SecurityContext>
    suspend fun hasPermission(context: SecurityContext, permission: String): Boolean
    suspend fun getUserRoles(userId: UUID): Flow<Role>
    suspend fun getRolePermissions(roleId: UUID): Flow<Permission>
}

data class Credentials(
    val username: String,
    val password: String,
    val clientId: UUID
)

data class AuthToken(
    val token: String,
    val expiresAt: java.time.Instant,
    val refreshToken: String
)

sealed class SecurityError {
    data class InvalidCredentials(val message: String) : SecurityError()
    data class InvalidToken(val message: String) : SecurityError()
    data class InsufficientPermissions(val message: String) : SecurityError()
    data class UserNotFound(val message: String) : SecurityError()
    data class ValidationFailed(val message: String) : SecurityError()  // Added this
}
