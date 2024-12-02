package org.blackerp.domain.ad.value

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import org.blackerp.shared.ValidationError

@JvmInline
value class ModuleName private constructor(val value: String) {
    companion object {
        fun create(value: String): Either<ValidationError, ModuleName> =
            when {
                !value.matches(Regex("^[a-z][a-z0-9-]*$")) ->
                    ValidationError.InvalidFormat(
                        "Module name must start with lowercase letter and contain only lowercase letters, numbers, and hyphens"
                    ).left()
                value.length !in 3..50 ->
                    ValidationError.InvalidLength("module name", 3, 50).left()
                else -> ModuleName(value).right()
            }
    }
}
