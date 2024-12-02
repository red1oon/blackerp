
package org.blackerp.domain.core.values

enum class DataType {
    STRING,
    INTEGER,
    DECIMAL,
    BOOLEAN,
    DATE,
    TIMESTAMP,
    BINARY;

    companion object {
        fun fromString(value: String): DataType =
            values().find { it.name.equals(value, ignoreCase = true) }
                ?: throw IllegalArgumentException("Invalid data type: $value")
    }
}
