package org.blackerp.domain.core.values

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import org.blackerp.domain.core.shared.ValidationError

@JvmInline
value class TableName private constructor(val value: String) {
    companion object {
        fun create(value: String): Either<ValidationError, TableName> =
            when {
                !value.matches(Regex("^[a-z][a-z0-9_]*$")) ->
                    ValidationError.InvalidFormat(
                        "Table name must start with lowercase letter and contain only lowercase letters, numbers, and underscores"
                    ).left()
                else -> TableName(value).right()
            }
    }
}
