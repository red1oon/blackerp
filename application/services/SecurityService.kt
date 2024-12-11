package org.blackerp.application.services

import org.springframework.stereotype.Service
import org.springframework.security.crypto.password.PasswordEncoder
import org.blackerp.domain.core.security.*
import arrow.core.Either
import kotlinx.coroutines.flow.*
import java.util.UUID

@Service
class SecurityService(
    private val passwordEncoder: PasswordEncoder
) : SecurityOperations {
    override suspend fun authenticate(credentials: Credentials): Either<SecurityError, AuthToken> =
        TODO()

    override suspend fun validateToken(token: String): Either<SecurityError, SecurityContext> =
        TODO()

    override suspend fun getUserRoles(userId: UUID): Flow<Role> =
        flow { }

    override suspend fun getRolePermissions(roleId: UUID): Flow<Permission> =
        flow { }

    override suspend fun hasPermission(context: SecurityContext, permission: String): Boolean =
        TODO()
}
