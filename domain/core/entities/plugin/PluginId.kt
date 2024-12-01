// domain-entities/src/main/kotlin/org/blackerp/plugin/PluginId.kt
package org.blackerp.plugin

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import org.blackerp.shared.ValidationError

@JvmInline
value class PluginId private constructor(val value: String) {
    companion object {
        fun create(value: String): Either<ValidationError, PluginId> = when {
            !value.matches(Regex("^[a-z][a-z0-9-]*$")) -> 
                ValidationError.InvalidFormat("Plugin ID must start with lowercase letter and contain only lowercase letters, numbers, and hyphens").left()
            value.length !in 3..50 -> 
                ValidationError.InvalidLength("plugin id", 3, 50).left()
            else -> PluginId(value).right()
        }
    }
}