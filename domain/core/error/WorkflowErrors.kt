package org.blackerp.domain.core.error

import org.blackerp.domain.core.DomainException

sealed class WorkflowError(
    override val message: String,
    override val code: String? = null,
    override val cause: Throwable? = null
) : DomainError(message, code, cause) {
    
    data class ValidationError(
        override val message: String,
        val field: String? = null
    ) : WorkflowError(message)
    
    data class NotFoundError(
        override val message: String,
        val id: String
    ) : WorkflowError(message)
    
    data class ProcessingError(
        override val message: String,
        override val cause: Throwable? = null
    ) : WorkflowError(message)
    
    data class ConcurrencyError(
        override val message: String,
        val entityId: String,
        val expectedVersion: Int,
        val actualVersion: Int
    ) : WorkflowError(message)
}
