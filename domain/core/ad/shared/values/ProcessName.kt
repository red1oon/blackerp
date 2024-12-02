package org.blackerp.domain.ad.shared.values

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import org.blackerp.shared.ValidationError

@JvmInline
value class ProcessName private constructor(val value: String) {
    companion object {
        fun create(value: String): Either<ValidationError, ProcessName> = when {
            !value.matches(Regex("^[a-z][a-z0-9_]*$")) -> 
                ValidationError.InvalidFormat("Process name must start with lowercase letter and contain only lowercase letters, numbers, and underscores").left()
            value.length !in 3..50 -> 
                ValidationError.InvalidLength("process name", 3, 50).left()
            else -> ProcessName(value).right()
        }
    }
}