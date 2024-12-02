package org.blackerp.domain.core.values

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import org.blackerp.shared.ValidationError

@JvmInline
value class DisplayName private constructor(val value: String) {
    companion object {
        fun create(value: String): Either<ValidationError, DisplayName> =
            when {
                value.isBlank() -> 
                    ValidationError.Required("display name").left()
                value.length !in 1..60 ->
                    ValidationError.InvalidLength("display name", 1, 60).left()
                else -> DisplayName(value).right()
            }
    }
}
