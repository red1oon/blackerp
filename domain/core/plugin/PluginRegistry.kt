package org.blackerp.domain.core.plugin

import arrow.core.Either
import org.blackerp.domain.core.error.PluginError

interface PluginRegistry {
    suspend fun register(plugin: PluginDefinition): Either<PluginError, Unit>
    suspend fun getPlugin(id: String): Either<PluginError, PluginDefinition?>
    suspend fun loadPlugins(): Either<PluginError, Unit>
    suspend fun startPlugin(id: String): Either<PluginError, Unit>
    suspend fun stopPlugin(id: String): Either<PluginError, Unit>
}
