// File: domain/core/error/WindowError.kt
package org.blackerp.domain.core.error

import java.util.UUID

sealed class WindowError(message: String) : DomainError(message) {
    data class ValidationFailed(val details: String) : WindowError(details)
    data class NotFound(val id: UUID) : WindowError("Window not found: $id")
    data class DuplicateWindow(val name: String) : WindowError("Window already exists: $name")
}
