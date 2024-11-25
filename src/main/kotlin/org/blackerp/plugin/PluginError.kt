package org.blackerp.plugin

import org.blackerp.shared.ValidationError

sealed interface PluginError {
    data class NotFound(val id: String) : PluginError
    data class ValidationFailed(val errors: List<ValidationError>) : PluginError
    data class DuplicatePlugin(val id: String) : PluginError
    data class IncompatibleVersion(val required: Version, val actual: Version) : PluginError
    data class InitializationFailed(val id: String, val cause: Throwable) : PluginError
    data class DiscoveryFailed(val message: String, val cause: Throwable? = null) : PluginError
    data class LoadFailed(val message: String) : PluginError
}
