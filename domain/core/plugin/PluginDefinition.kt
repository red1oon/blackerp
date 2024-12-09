package org.blackerp.domain.core.plugin

import org.blackerp.domain.core.Version

interface PluginDefinition {
    val id: String
    val version: Version
    val dependencies: Set<PluginDependency>
    
    fun initialize(context: PluginContext)
    fun start()
    fun stop()
}

data class PluginDependency(
    val pluginId: String,
    val version: Version
)

interface PluginContext {
    fun getPlugin(id: String): PluginDefinition?
    fun getConfiguration(key: String): String?
}
