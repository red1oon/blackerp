package org.blackerp.domain.core.values

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import org.blackerp.shared.ValidationError
import java.math.BigDecimal

@JvmInline
value class Amount private constructor(val value: BigDecimal) {
    companion object {
        fun create(value: BigDecimal): Either<ValidationError, Amount> =
            when {
                value < BigDecimal.ZERO ->
                    ValidationError.InvalidValue("Amount cannot be negative").left()
                else -> Amount(value).right()
            }
    }
}
