package org.blackerp.plugin.registry

import arrow.core.Either
import org.blackerp.plugin.Plugin
import org.blackerp.plugin.PluginError
import org.blackerp.plugin.PluginId

interface PluginRegistry {
    suspend fun register(plugin: Plugin): Either<PluginError, Unit>
    suspend fun unregister(pluginId: PluginId): Either<PluginError, Unit>
    suspend fun getPlugin(pluginId: PluginId): Either<PluginError, Plugin>
    fun getPlugins(): List<Plugin>
}
