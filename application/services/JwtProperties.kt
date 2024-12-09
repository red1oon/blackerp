package org.blackerp.application.services

data class JwtProperties(
    val secret: String,
    val expirationHours: Int = 24
)
