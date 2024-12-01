package org.blackerp.domain.table.relationship.value

enum class DeleteRule {
    RESTRICT,
    CASCADE,
    SET_NULL,
    NO_ACTION;
    
    companion object {
        fun fromString(value: String): DeleteRule =
            values().find { it.name.equals(value, ignoreCase = true) }
                ?: throw IllegalArgumentException("Invalid delete rule: $value")
    }
}
