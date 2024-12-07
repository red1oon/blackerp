package org.blackerp.application.api.auth.dto

import java.util.UUID

data class LoginRequest(
    val username: String,
    val password: String,
    val clientId: UUID
)

data class LoginResponse(
    val token: String,
    val refreshToken: String,
    val expiresAt: Long,
    val user: UserDto
)

data class UserDto(
    val id: UUID,
    val username: String,
    val email: String,
    val clientId: UUID,
    val organizationId: UUID?,
    val roles: List<String>
)

data class RefreshTokenRequest(
    val refreshToken: String
)
