package org.blackerp.domain.core.security.permission

enum class Permission {
    READ,
    CREATE,
    UPDATE,
    DELETE,
    ADMIN;

    companion object {
        fun fromString(value: String): Permission =
            values().find { it.name.equals(value, ignoreCase = true) }
                ?: throw IllegalArgumentException("Invalid permission: $value")
    }
}
