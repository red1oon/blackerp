// File: src/main/kotlin/org/blackerp/domain/values/ColumnName.kt
package org.blackerp.domain.core.values

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import org.blackerp.domain.core.shared.ValidationError

@JvmInline
value class ColumnName private constructor(val value: String) {
    companion object {
        fun create(value: String): Either<ValidationError, ColumnName> =
            when {
                !value.matches(Regex("^[a-z][a-z0-9_]*$")) ->
                    ValidationError.InvalidFormat("Column name must start with lowercase letter and contain only lowercase letters, numbers, and underscores").left()
                value.length > 30 ->
                    ValidationError.InvalidLength("column name", 1, 30).left()
                else -> ColumnName(value).right()
            }
    }
}