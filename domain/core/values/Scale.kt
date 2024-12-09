
package org.blackerp.domain.core.values

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import org.blackerp.domain.core.shared.ValidationError
import org.blackerp.domain.core.shared.ValidationError.InvalidValue

@JvmInline
value class Scale private constructor(val value: Int) {
    companion object {
        fun create(value: Int): Either<ValidationError, Scale> =
            when {
                value < 0 ->
                    ValidationError.InvalidValue("Scale cannot be negative").left()
                value > 10 ->
                    ValidationError.InvalidValue("Scale cannot exceed 10").left()
                else -> Scale(value).right()
            }
    }
}
