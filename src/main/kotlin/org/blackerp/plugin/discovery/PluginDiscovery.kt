package org.blackerp.plugin.discovery

import arrow.core.Either
import org.blackerp.plugin.Plugin
import org.blackerp.plugin.PluginError
import java.nio.file.Path

interface PluginDiscovery {
    suspend fun discoverPlugins(directory: Path): Either<PluginError, List<Plugin>>
    suspend fun loadPlugin(jarPath: Path): Either<PluginError, Plugin>
}
