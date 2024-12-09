package org.blackerp.application.services.auth

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Service
import org.blackerp.domain.core.security.SecurityContext
import org.blackerp.domain.core.security.User
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Date
import java.util.UUID

@Service
class JwtService(
    private val jwtProperties: JwtProperties
) {
    private val key = Keys.hmacShaKeyFor(jwtProperties.secret.toByteArray())

    fun generateToken(user: User, context: SecurityContext): String {
        val now = Instant.now()
        val expiry = now.plus(jwtProperties.expirationHours.toLong(), ChronoUnit.HOURS)

        return Jwts.builder()
            .setSubject(user.id.toString())
            .claim("username", user.username)
            .claim("clientId", user.clientId.toString())
            .claim("orgId", user.organizationId?.toString())
            .claim("roles", user.roles.map { it.name })
            .setIssuedAt(Date.from(now))
            .setExpiration(Date.from(expiry))
            .signWith(key)
            .compact()
    }

    fun validateToken(token: String): Claims? {
        return try {
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .body
        } catch (e: Exception) {
            null
        }
    }

    fun extractUserId(token: String): UUID? =
        validateToken(token)?.subject?.let { UUID.fromString(it) }
}
