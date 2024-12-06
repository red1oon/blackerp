// domain/core/ad/workflow/WorkflowError.kt
package org.blackerp.domain.core.ad.workflow

import org.blackerp.domain.core.error.DomainError
import java.util.UUID

sealed class WorkflowError(
    override val message: String,
    override val code: String? = null,
    override val cause: Throwable? = null
) : DomainError(message, code, cause) {
    
    data class NotFound(val id: UUID) :
        WorkflowError("Workflow node not found: $id", "WF_NOT_FOUND")
    
    data class ValidationFailed(val details: String) :
        WorkflowError("Workflow validation failed: $details", "WF_VALIDATION_FAILED")
    
    data class CyclicDependency(val nodeIds: List<UUID>) :
        WorkflowError("Cyclic dependency detected in workflow: ${nodeIds.joinToString()}", "WF_CYCLIC_DEPENDENCY")
}
