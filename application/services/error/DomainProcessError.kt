package org.blackerp.application.services.error

import org.blackerp.domain.core.error.DomainError
import org.blackerp.domain.core.ad.process.ProcessError

sealed class DomainProcessError(override val message: String) : DomainError(message) {
    data class ValidationFailed(val details: String) : DomainProcessError("Validation failed: $details")
    data class ExecutionFailed(val details: String) : DomainProcessError("Execution failed: $details")
    data class NotFound(val id: String) : DomainProcessError("Process not found: $id")
    data class UnexpectedError(override val message: String) : DomainProcessError(message)

    companion object {
        fun fromProcessError(error: ProcessError): DomainProcessError = when (error) {
            is ProcessError.ValidationFailed -> ValidationFailed(error.details)
            is ProcessError.ExecutionFailed -> ExecutionFailed(error.details)
            is ProcessError.NotFound -> NotFound(error.id.toString())
            is ProcessError.UnexpectedError -> UnexpectedError(error.message)
        }
    }
}
