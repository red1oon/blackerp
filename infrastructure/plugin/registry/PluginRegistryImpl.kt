package org.blackerp.infrastructure.plugin.registry

import org.springframework.stereotype.Component
import org.blackerp.domain.core.plugin.*
import org.blackerp.domain.core.error.PluginError
import arrow.core.Either
import arrow.core.right
import arrow.core.left
import org.slf4j.LoggerFactory

@Component
class PluginRegistryImpl : PluginRegistry {
    private val logger = LoggerFactory.getLogger(PluginRegistryImpl::class.java)
    private val plugins = mutableMapOf<String, PluginDefinition>()
    private val pluginContext = object : PluginContext {
        override fun getPlugin(id: String) = plugins[id]
        override fun getConfiguration(key: String): String? = null // TODO: Implement configuration
    }

    override suspend fun register(plugin: PluginDefinition): Either<PluginError, Unit> = 
        try {
            plugins[plugin.id] = plugin
            Unit.right()
        } catch (e: Exception) {
            logger.error("Failed to register plugin: ${plugin.id}", e)
            PluginError.RegistrationFailed(plugin.id, e).left()
        }

    override suspend fun getPlugin(id: String): Either<PluginError, PluginDefinition?> =
        Either.catch { plugins[id] }
            .mapLeft { PluginError.LoadFailed("Failed to get plugin $id: ${it.message}") }

    override suspend fun loadPlugins(): Either<PluginError, Unit> =
        Either.catch {
            // TODO: Implement plugin discovery and loading
            Unit
        }.mapLeft { PluginError.LoadFailed(it.message ?: "Unknown error during plugin loading") }

    override suspend fun startPlugin(id: String): Either<PluginError, Unit> =
        getPlugin(id).flatMap { plugin ->
            Either.catch {
                plugin?.start()
                Unit
            }.mapLeft { PluginError.StartupFailed(id, it) }
        }

    override suspend fun stopPlugin(id: String): Either<PluginError, Unit> =
        getPlugin(id).flatMap { plugin ->
            Either.catch {
                plugin?.stop()
                Unit
            }.mapLeft { PluginError.ShutdownFailed(id, it) }
        }
}
