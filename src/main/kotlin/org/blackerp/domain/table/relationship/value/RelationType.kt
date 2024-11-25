package org.blackerp.domain.table.relationship.value

enum class RelationType {
    ONE_TO_ONE,
    ONE_TO_MANY,
    MANY_TO_MANY;

    companion object {
        fun fromString(value: String): RelationType =
            values().find { it.name.equals(value, ignoreCase = true) }
                ?: throw IllegalArgumentException("Invalid relationship type: $value")
    }
}
