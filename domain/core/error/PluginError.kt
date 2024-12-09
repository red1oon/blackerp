package org.blackerp.domain.core.error

sealed class PluginError {
    data class NotFound(val id: String) : PluginError()
    data class RegistrationFailed(val id: String, val cause: Throwable) : PluginError()
    data class LoadFailed(val message: String) : PluginError()
    data class StartupFailed(val id: String, val cause: Throwable) : PluginError()
    data class ShutdownFailed(val id: String, val cause: Throwable) : PluginError()
    data class DependencyError(val id: String, val missingDependency: String) : PluginError()
}
