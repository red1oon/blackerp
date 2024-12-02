
package org.blackerp.domain.core.values

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import org.blackerp.shared.ValidationError

@JvmInline
value class Precision private constructor(val value: Int) {
    companion object {
        fun create(value: Int): Either<ValidationError, Precision> =
            when {
                value < 0 ->
                    ValidationError.InvalidValue("Precision cannot be negative").left()
                value > 20 ->
                    ValidationError.InvalidValue("Precision cannot exceed 20").left()
                else -> Precision(value).right()
            }
    }
}
