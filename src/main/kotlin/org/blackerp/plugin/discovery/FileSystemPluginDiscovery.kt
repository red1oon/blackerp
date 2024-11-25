package org.blackerp.plugin.discovery

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import org.blackerp.plugin.Plugin
import org.blackerp.plugin.PluginError
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.extension
import kotlin.io.path.isRegularFile

class FileSystemPluginDiscovery : PluginDiscovery {
    override suspend fun discoverPlugins(directory: Path): Either<PluginError, List<Plugin>> =
        try {
            Files.walk(directory)
                .filter { it.isRegularFile() && it.extension == "jar" }
                .use { paths ->
                    paths.toList()
                        .map { loadPlugin(it) }
                        .filter { it.isRight() }
                        .map { it.getOrNull()!! }
                        .toList()
                        .right()
                }
        } catch (e: Exception) {
            PluginError.DiscoveryFailed("Failed to discover plugins: ${e.message}", e).left()
        }

    override suspend fun loadPlugin(jarPath: Path): Either<PluginError, Plugin> =
        try {
            // Placeholder for actual JAR loading logic
            // Will be implemented with proper classloading
            PluginError.LoadFailed("Plugin loading not yet implemented").left()
        } catch (e: Exception) {
            PluginError.LoadFailed("Failed to load plugin: ${e.message}").left()
        }
}
