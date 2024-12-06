package org.blackerp.domain.core.ad.process

import org.blackerp.domain.core.error.DomainError
import java.util.UUID

sealed class ProcessError(message: String) : DomainError(message) {
    data class ValidationFailed(val details: String) : ProcessError("Process validation failed: $details")
    data class ExecutionFailed(val details: String) : ProcessError("Process execution failed: $details")
    data class NotFound(val id: UUID) : ProcessError("Process not found: $id")
    data class UnexpectedError(override val message: String) : ProcessError(message)
}
