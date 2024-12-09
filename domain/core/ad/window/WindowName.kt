
package org.blackerp.domain.core.ad.window

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import org.blackerp.domain.core.shared.ValidationError

@JvmInline
value class WindowName private constructor(val value: String) {
    companion object {
        fun create(value: String): Either<ValidationError, WindowName> =
            when {
                !value.matches(Regex("^[a-z][a-z0-9_]*$")) ->
                    ValidationError.InvalidFormat(
                        "Window name must start with lowercase letter and contain only lowercase letters, numbers, and underscores"
                    ).left()
                value.length !in 3..50 ->
                    ValidationError.InvalidLength("window name", 3, 50).left()
                else -> WindowName(value).right()
            }
    }
}
