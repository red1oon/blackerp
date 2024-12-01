package org.blackerp.domain.table.relationship.value

enum class UpdateRule {
    RESTRICT,
    CASCADE,
    SET_NULL,
    NO_ACTION;
    
    companion object {
        fun fromString(value: String): UpdateRule =
            values().find { it.name.equals(value, ignoreCase = true) }
                ?: throw IllegalArgumentException("Invalid update rule: $value")
    }
}
