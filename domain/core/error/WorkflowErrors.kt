package org.blackerp.domain.core.error

sealed class WorkflowError : DomainError {
    data class ValidationError(
        override val message: String,
        val field: String? = null
    ) : WorkflowError()

    data class NotFoundError(
        override val message: String,
        val id: String
    ) : WorkflowError()

    data class ProcessingError(
        override val message: String,
        val cause: Throwable? = null
    ) : WorkflowError()

    data class ConcurrencyError(
        override val message: String,
        val entityId: String,
        val expectedVersion: Int,
        val actualVersion: Int
    ) : WorkflowError()
}
