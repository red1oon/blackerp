package org.blackerp.domain.error

sealed interface DomainError {
    val message: String
    
    data class ValidationError(
        override val message: String,
        val field: String? = null
    ) : DomainError

    data class NotFoundError(
        override val message: String,
        val id: String
    ) : DomainError
    
    data class SecurityError(
        override val message: String,
        val reason: String
    ) : DomainError
    
    data class CacheError(
        override val message: String,
        val cause: Throwable? = null
    ) : DomainError
    
    data class QueryError(
        override val message: String,
        val reason: String
    ) : DomainError

    data class SystemError(
        override val message: String,
        val cause: Throwable? = null
    ) : DomainError
}
