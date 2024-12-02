package org.blackerp.domain.core.values

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import org.blackerp.shared.ValidationError

@JvmInline
value class Currency private constructor(val code: String) {
    companion object {
        fun create(code: String): Either<ValidationError, Currency> =
            when {
                !code.matches(Regex("^[A-Z]{3}$")) ->
                    ValidationError.InvalidFormat("Currency code must be 3 uppercase letters").left()
                else -> Currency(code).right()
            }
    }
}
