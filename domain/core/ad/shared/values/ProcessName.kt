package org.blackerp.domain.core.ad.shared.values

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import org.blackerp.domain.core.shared.ValidationError

@JvmInline
value class ProcessName private constructor(val value: String) {
    companion object {
        fun create(value: String): Either<ValidationError, ProcessName> =
            when {
                value.isBlank() -> 
                    ValidationError.InvalidFormat("Process name cannot be blank").left()
                value.length > 50 -> 
                    ValidationError.InvalidLength("Process name", 1, 50).left()
                else -> ProcessName(value).right()
            }
    }
}
