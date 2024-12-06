// domain/core/ad/docaction/DocActionError.kt
package org.blackerp.domain.core.ad.docaction

import org.blackerp.domain.core.error.DomainError 

sealed class DocActionError(
    override val message: String,
    override val code: String? = null,
    override val cause: Throwable? = null
) : DomainError(message, code, cause) {

    data class ExecutionFailed(val details: String) : 
        DocActionError("Action execution failed: $details", "DOC_EXEC_FAILED")
    
    data class ValidationFailed(val details: String) : 
        DocActionError("Action validation failed: $details", "DOC_VAL_FAILED")
    
    data class NotFound(override val code: String) : 
        DocActionError("Action not found: $code", "DOC_ACTION_NOT_FOUND")
}
