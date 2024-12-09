package org.blackerp.domain.core.values

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import org.blackerp.domain.core.shared.ValidationError

@JvmInline
value class TabName private constructor(val value: String) {
    companion object {
        fun create(value: String): Either<ValidationError, TabName> = when {
            !value.matches(Regex("^[a-z][a-z0-9_]*$")) ->
                ValidationError.InvalidFormat("Tab name must start with lowercase letter and contain only lowercase letters, numbers, and underscores").left()
            value.length !in 3..50 ->
                ValidationError.InvalidLength("tab name", 3, 50).left()
            else -> TabName(value).right()
        }
    }
}
