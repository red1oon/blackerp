// domain/core/ad/docstatus/DocStatusError.kt
package org.blackerp.domain.core.ad.docstatus

import org.blackerp.domain.core.error.DomainError
import java.util.UUID

sealed class DocStatusError(
    override val message: String,
    override val code: String? = null, 
    override val cause: Throwable? = null
) : DomainError(message, code, cause) {
    
    data class NotFound(
        val id: UUID
    ) : DocStatusError(
        "Document not found: $id",
        "DOC_NOT_FOUND"
    )
    
    data class ValidationFailed(
        val details: String
    ) : DocStatusError(
        "Validation failed: $details",
        "DOC_VALIDATION_FAILED"
    )

    data class StatusTransitionInvalid(
        val currentStatus: String,
        val targetStatus: String
    ) : DocStatusError(
        "Invalid status transition from $currentStatus to $targetStatus",
        "DOC_STATUS_INVALID"
    )
}
