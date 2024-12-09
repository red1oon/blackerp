// domain/core/ad/document/DocumentError.kt
package org.blackerp.domain.core.ad.document

import org.blackerp.domain.core.error.DomainError
import java.util.UUID

sealed class DocumentError(
    override val message: String,
    override val code: String? = null,
    override val cause: Throwable? = null
) : DomainError(message, code, cause) {
    
    data class NotFound(val id: UUID) :
        DocumentError("Document not found: $id", "DOC_NOT_FOUND")
    
    data class ValidationFailed(val details: String) :
        DocumentError("Validation failed: $details", "DOC_VALIDATION_FAILED")
    
    data class StatusTransitionInvalid(
        val currentStatus: DocumentStatus,
        val targetStatus: DocumentStatus
    ) : DocumentError(
        "Invalid status transition from $currentStatus to $targetStatus",
        "DOC_STATUS_INVALID"
    )
}
