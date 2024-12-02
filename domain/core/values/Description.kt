package org.blackerp.domain.values

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import org.blackerp.shared.ValidationError

@JvmInline
value class Description private constructor(val value: String) {
    companion object {
        fun create(value: String): Either<ValidationError, Description> =
            when {
                value.length > 255 ->
                    ValidationError.InvalidLength("description", 0, 255).left()
                else -> Description(value).right()
            }
    }
}