
package org.blackerp.domain.core.values

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import org.blackerp.domain.core.shared.ValidationError

@JvmInline
value class Length private constructor(val value: Int) {
    companion object {
        fun create(value: Int): Either<ValidationError, Length> =
            when {
                value <= 0 ->
                    ValidationError.InvalidValue("Length must be positive").left()
                else -> Length(value).right()
            }
    }
}
