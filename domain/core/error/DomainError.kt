package org.blackerp.domain.error

import arrow.core.Either
import arrow.core.left
import arrow.core.right

sealed interface DomainError {
    val message: String
    
    data class ValidationError(
        override val message: String,
        val field: String? = null,
        val details: Map<String, Any?> = emptyMap()
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

// Enhanced validation result type that works with existing Either pattern
sealed class ValidationResult<out T> {
    data class Valid<T>(val value: T) : ValidationResult<T>()
    data class Invalid(val errors: List<DomainError>) : ValidationResult<Nothing>()

    fun toEither(): Either<List<DomainError>, T> = when (this) {
        is Valid -> value.right()
        is Invalid -> errors.left()
    }
}
