package org.blackerp.plugin

import arrow.core.Either

interface ExtensionRegistry {
    /**
     * Register an extension for a specific extension point
     * @param extension The extension implementation
     * @return Either a registration error or Unit on success
     */
    suspend fun <T : Extension> register(extension: T): Either<PluginError, Unit>
    
    /**
     * Get all registered extensions for a specific type
     * @return List of registered extensions
     */
    fun <T : Extension> getExtensions(type: Class<T>): List<T>
}
