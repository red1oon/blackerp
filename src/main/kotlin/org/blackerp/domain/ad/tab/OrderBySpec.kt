
package org.blackerp.domain.ad.tab

import org.blackerp.domain.values.ColumnName

data class OrderBySpec(
    val column: ColumnName,
    val direction: SortDirection
)

enum class SortDirection {
    ASC, DESC;

    companion object {
        fun fromString(value: String): SortDirection =
            values().find { it.name.equals(value, ignoreCase = true) }
                ?: throw IllegalArgumentException("Invalid sort direction: $value")
    }
}
