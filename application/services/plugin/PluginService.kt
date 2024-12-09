package org.blackerp.application.services.plugin

import org.springframework.stereotype.Service
import org.blackerp.domain.core.plugin.PluginRegistry
import org.blackerp.domain.core.error.PluginError
import arrow.core.Either
import org.slf4j.LoggerFactory

@Service
class PluginService(
    private val pluginRegistry: PluginRegistry
) {
    private val logger = LoggerFactory.getLogger(PluginService::class.java)

    suspend fun loadPlugins(): Either<PluginError, Unit> {
        logger.info("Loading plugins...")
        return pluginRegistry.loadPlugins()
    }

    suspend fun getPlugin(id: String) = pluginRegistry.getPlugin(id)

    suspend fun startPlugin(id: String) = pluginRegistry.startPlugin(id)

    suspend fun stopPlugin(id: String) = pluginRegistry.stopPlugin(id)
}
