package org.blackerp.plugin

import arrow.core.Either

interface Plugin {
    val metadata: PluginMetadata
    
    /**
     * Initialize the plugin with provided context
     * @return Either an initialization error or Unit on success
     */
    suspend fun initialize(): Either<PluginError, Unit>
    
    /**
     * Register plugin extensions with the registry
     * @param registry Extension point registry
     * @return Either a registration error or Unit on success
     */
    suspend fun registerExtensions(registry: ExtensionRegistry): Either<PluginError, Unit>
    
    /**
     * Clean up plugin resources
     * @return Either a cleanup error or Unit on success
     */
    suspend fun shutdown(): Either<PluginError, Unit>
}
