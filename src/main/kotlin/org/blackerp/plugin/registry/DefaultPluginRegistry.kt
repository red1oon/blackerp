package org.blackerp.plugin.registry

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import org.blackerp.plugin.Plugin
import org.blackerp.plugin.PluginError
import org.blackerp.plugin.PluginId
import java.util.concurrent.ConcurrentHashMap

class DefaultPluginRegistry : PluginRegistry {
    private val plugins = ConcurrentHashMap<PluginId, Plugin>()

    override suspend fun register(plugin: Plugin): Either<PluginError, Unit> =
        when {
            plugins.containsKey(plugin.metadata.id) -> 
                PluginError.DuplicatePlugin(plugin.metadata.id.value).left()
            else -> {
                plugins[plugin.metadata.id] = plugin
                Unit.right()
            }
        }

    override suspend fun unregister(pluginId: PluginId): Either<PluginError, Unit> =
        plugins.remove(pluginId)?.let {
            Unit.right()
        } ?: PluginError.NotFound(pluginId.value).left()

    override suspend fun getPlugin(pluginId: PluginId): Either<PluginError, Plugin> =
        plugins[pluginId]?.right() ?: PluginError.NotFound(pluginId.value).left()

    override fun getPlugins(): List<Plugin> = plugins.values.toList()
}
