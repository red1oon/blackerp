// domain/core/reference/ReferenceError.kt
package org.blackerp.domain.core.reference

import org.blackerp.domain.core.error.DomainError
import java.util.UUID

sealed class ReferenceError(
    override val message: String,
    override val code: String? = null,
    override val cause: Throwable? = null
) : DomainError(message, code, cause) {
    
    data class NotFound(val id: UUID) : 
        ReferenceError(message = "Reference not found: $id", code = "REF_NOT_FOUND")
    
    data class ValidationFailed(val details: String) :
        ReferenceError(message = "Reference validation failed: $details", code = "REF_VALIDATION_FAILED")
        
    data class CacheError(val operation: String, val details: String) :
        ReferenceError(
            message = "Cache operation '$operation' failed: $details",
            code = "REF_CACHE_ERROR"
        )
}

enum class CacheStrategy {
    NONE,
    READ_WRITE,
    READ_ONLY
}
