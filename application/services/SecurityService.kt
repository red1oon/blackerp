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
