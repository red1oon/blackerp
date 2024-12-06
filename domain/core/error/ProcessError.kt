package org.blackerp.domain.core.error

sealed class ProcessError(message: String) : DomainError(message) {
    data class ValidationFailed(val details: String) : ProcessError("Process validation failed: $details")
    data class ExecutionFailed(val details: String) : ProcessError("Process execution failed: $details")
    data class NotFound(val id: String) : ProcessError("Process not found: $id")
}
