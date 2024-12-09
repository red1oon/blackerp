package org.blackerp.application.services.auth

import org.springframework.stereotype.Service
import org.springframework.security.crypto.password.PasswordEncoder
import org.blackerp.domain.core.security.*
import org.blackerp.application.api.auth.dto.*
import arrow.core.Either
import arrow.core.left
import arrow.core.right
import java.time.Instant
import org.slf4j.LoggerFactory

@Service
class AuthService(
    private val jwtService: JwtService,
    private val passwordEncoder: PasswordEncoder,
    private val userRepository: UserRepository, // To be implemented
    private val securityService: SecurityService
) {
    private val logger = LoggerFactory.getLogger(AuthService::class.java)

    suspend fun authenticate(credentials: Credentials): Either<SecurityError, LoginResponse> {
        return try {
            // For POC, using mock validation
            // TODO: Implement actual user validation
            val user = User(
                username = credentials.username,
                email = "${credentials.username}@example.com",
                clientId = credentials.clientId,
                organizationId = null,
                roles = emptySet()
            )
            
            val context = SecurityContext(
                user = user,
                clientId = credentials.clientId,
                organizationId = null,
                roles = emptySet()
            )

            val token = jwtService.generateToken(user, context)
            val refreshToken = "refresh-${token}" // TODO: Implement proper refresh token

            LoginResponse(
                token = token,
                refreshToken = refreshToken,
                expiresAt = Instant.now().plusSeconds(3600).epochSecond,
                user = UserDto(
                    id = user.id,
                    username = user.username,
                    email = user.email,
                    clientId = user.clientId,
                    organizationId = user.organizationId,
                    roles = user.roles.map { it.name }
                )
            ).right()
        } catch (e: Exception) {
            logger.error("Authentication failed", e)
            SecurityError.InvalidCredentials("Invalid credentials").left()
        }
    }

    suspend fun refreshToken(refreshToken: String): Either<SecurityError, LoginResponse> {
        // TODO: Implement refresh token logic
        return SecurityError.InvalidToken("Refresh token not implemented").left()
    }
}
