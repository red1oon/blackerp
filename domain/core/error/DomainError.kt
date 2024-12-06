// domain/core/error/DomainError.kt
package org.blackerp.domain.core.error

abstract class DomainError(
    open val message: String,
    open val code: String? = null,
    open val cause: Throwable? = null
)
