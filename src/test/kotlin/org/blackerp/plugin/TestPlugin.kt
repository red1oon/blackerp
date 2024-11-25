// Classpath: src/test/kotlin/org/blackerp/plugin/TestPlugin.kt
package org.blackerp.plugin

import arrow.core.Either
import arrow.core.right

class TestPlugin(
    override val metadata: PluginMetadata,
    private val extensions: List<Extension> = emptyList()
) : Plugin {
    var initialized = false
    var shutdown = false
    
    override suspend fun initialize(): Either<PluginError, Unit> {
        initialized = true
        return Unit.right()
    }
    
    override suspend fun registerExtensions(registry: ExtensionRegistry): Either<PluginError, Unit> {
        extensions.forEach { extension ->
            registry.register(extension)
        }
        return Unit.right()
    }
    
    override suspend fun shutdown(): Either<PluginError, Unit> {
        shutdown = true
        return Unit.right()
    }
}