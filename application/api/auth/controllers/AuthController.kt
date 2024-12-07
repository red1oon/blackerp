package org.blackerp.application.api.auth.controllers

import org.springframework.web.bind.annotation.*
import org.springframework.http.ResponseEntity
import org.blackerp.application.api.auth.dto.*
import org.blackerp.application.services.auth.AuthService
import org.blackerp.domain.core.security.Credentials
import jakarta.validation.Valid

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService
) {
    @PostMapping("/login")
    suspend fun login(
        @Valid @RequestBody request: LoginRequest
    ): ResponseEntity<LoginResponse> =
        authService.authenticate(
            Credentials(
                username = request.username,
                password = request.password,
                clientId = request.clientId
            )
        ).fold(
            { ResponseEntity.badRequest().build() },
            { ResponseEntity.ok(it) }
        )

    @PostMapping("/refresh")
    suspend fun refresh(
        @Valid @RequestBody request: RefreshTokenRequest
    ): ResponseEntity<LoginResponse> =
        authService.refreshToken(request.refreshToken).fold(
            { ResponseEntity.badRequest().build() },
            { ResponseEntity.ok(it) }
        )
}
