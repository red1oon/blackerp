#!/bin/bash

# generate_security_management.sh
echo "Generating security components..."

# 1. Security Core
cat > domain/core/security/SecurityModel.kt << 'EOF'
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
EOF

# 2. Security Operations
cat > domain/core/security/SecurityOperations.kt << 'EOF'
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
}
EOF

# 3. Security Service
cat > application/services/SecurityService.kt << 'EOF'
package org.blackerp.application.services

import org.springframework.stereotype.Service
import org.springframework.security.crypto.password.PasswordEncoder
import org.blackerp.domain.core.security.*
import arrow.core.Either
import arrow.core.right
import arrow.core.left
import org.slf4j.LoggerFactory
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import java.util.UUID
import java.time.Instant
import java.time.temporal.ChronoUnit

@Service
class SecurityService(
    private val passwordEncoder: PasswordEncoder,
    private val userRepository: UserRepository,
    private val jwtProperties: JwtProperties
) : SecurityOperations {

    private val logger = LoggerFactory.getLogger(SecurityService::class.java)
    private val secretKey = Keys.hmacShaKeyFor(jwtProperties.secret.toByteArray())

    override suspend fun authenticate(credentials: Credentials): Either<SecurityError, AuthToken> {
        logger.debug("Authenticating user: ${credentials.username}")
        
        return userRepository.findByUsername(credentials.username)?.let { user ->
            if (!user.isActive) {
                SecurityError.InvalidCredentials("Account is inactive").left()
            } else if (passwordEncoder.matches(credentials.password, user.password)) {
                generateToken(user).right()
            } else {
                SecurityError.InvalidCredentials("Invalid password").left()
            }
        } ?: SecurityError.UserNotFound("User not found").left()
    }

    override suspend fun validateToken(token: String): Either<SecurityError, SecurityContext> {
        return try {
            val claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
            
            val userId = UUID.fromString(claims.body["userId"].toString())
            userRepository.findById(userId)?.let { user ->
                SecurityContext(
                    user = user,
                    clientId = user.clientId,
                    organizationId = user.organizationId,
                    roles = user.roles
                ).right()
            } ?: SecurityError.UserNotFound("User not found").left()
        } catch (e: Exception) {
            logger.error("Token validation failed", e)
            SecurityError.InvalidToken("Invalid or expired token").left()
        }
    }

    private fun generateToken(user: User): AuthToken {
        val now = Instant.now()
        val expiresAt = now.plus(jwtProperties.expirationHours.toLong(), ChronoUnit.HOURS)
        
        val token = Jwts.builder()
            .setSubject(user.username)
            .claim("userId", user.id.toString())
            .claim("clientId", user.clientId.toString())
            .claim("roles", user.roles.map { it.name })
            .setIssuedAt(java.util.Date.from(now))
            .setExpiration(java.util.Date.from(expiresAt))
            .signWith(secretKey)
            .compact()

        return AuthToken(
            token = token,
            expiresAt = expiresAt,
            refreshToken = generateRefreshToken()
        )
    }

    private fun generateRefreshToken(): String = UUID.randomUUID().toString()
}

data class JwtProperties(
    val secret: String,
    val expirationHours: Int = 24
)
EOF

# 4. Security Filter
cat > application/security/SecurityFilter.kt << 'EOF'
package org.blackerp.application.security

import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import org.springframework.stereotype.Component
import org.springframework.http.HttpStatus
import org.blackerp.application.services.SecurityService
import reactor.core.publisher.Mono
import org.slf4j.LoggerFactory

@Component
class SecurityFilter(
    private val securityService: SecurityService
) : WebFilter {
    private val logger = LoggerFactory.getLogger(SecurityFilter::class.java)
    private val publicPaths = setOf("/api/auth/login", "/api/auth/refresh")

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val path = exchange.request.path.value()
        
        if (publicPaths.any { path.startsWith(it) }) {
            return chain.filter(exchange)
        }

        val token = exchange.request.headers.getFirst("Authorization")?.removePrefix("Bearer ")
        
        if (token == null) {
            exchange.response.statusCode = HttpStatus.UNAUTHORIZED
            return exchange.response.setComplete()
        }

        return Mono.fromCallable {
            securityService.validateToken(token)
        }.flatMap { result ->
            result.fold(
                { error ->
                    logger.error("Authentication failed: $error")
                    exchange.response.statusCode = HttpStatus.UNAUTHORIZED
                    exchange.response.setComplete()
                },
                { context ->
                    exchange.attributes["securityContext"] = context
                    chain.filter(exchange)
                }
            )
        }
    }
}
EOF

echo "Security components generated successfully!" 