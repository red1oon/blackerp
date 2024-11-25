// Classpath: src/test/kotlin/org/blackerp/plugin/InMemoryExtensionRegistry.kt
package org.blackerp.plugin

import arrow.core.Either
import arrow.core.right
import java.util.concurrent.ConcurrentHashMap

class InMemoryExtensionRegistry : ExtensionRegistry {
    private val extensions = ConcurrentHashMap<Class<*>, MutableList<Extension>>()
    
    override suspend fun <T : Extension> register(extension: T): Either<PluginError, Unit> {
        extensions.computeIfAbsent(extension::class.java) { mutableListOf() }
            .add(extension)
        return Unit.right()
    }
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : Extension> getExtensions(type: Class<T>): List<T> =
        extensions[type]?.map { it as T } ?: emptyList()
}