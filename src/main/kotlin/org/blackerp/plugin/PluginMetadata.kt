package org.blackerp.plugin

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import org.blackerp.shared.ValidationError

data class PluginMetadata private constructor(
    val id: PluginId,
    val version: Version,
    val name: String,
    val description: String,
    val vendor: String
) {
    companion object {
        fun create(
            id: PluginId,
            version: Version,
            name: String,
            description: String,
            vendor: String
        ): Either<ValidationError, PluginMetadata> =
            when {
                name.isBlank() ->
                    ValidationError.Required("name").left()
                name.length !in 3..100 ->
                    ValidationError.InvalidLength("name", 3, 100).left()
                description.length > 500 ->
                    ValidationError.InvalidLength("description", 0, 500).left()
                vendor.isBlank() ->
                    ValidationError.Required("vendor").left()
                vendor.length !in 3..100 ->
                    ValidationError.InvalidLength("vendor", 3, 100).left()
                else -> PluginMetadata(id, version, name, description, vendor).right()
            }
    }
}
