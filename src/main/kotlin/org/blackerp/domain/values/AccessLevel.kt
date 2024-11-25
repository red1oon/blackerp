package org.blackerp.domain.values

enum class AccessLevel {
    SYSTEM,
    CLIENT,
    ORGANIZATION,
    CLIENT_ORGANIZATION;
    
    companion object {
        fun fromString(value: String): AccessLevel =
            values().find { it.name.equals(value, ignoreCase = true) }
                ?: throw IllegalArgumentException("Invalid access level: $value")
    }
}
