package org.blackerp.domain.core.ad.workflow

import arrow.core.Either
import kotlinx.coroutines.flow.Flow
import org.blackerp.domain.core.error.DomainError
import org.blackerp.domain.core.values.DisplayName
import org.blackerp.domain.core.values.Description
import org.blackerp.domain.core.shared.ValidationError
import org.blackerp.domain.core.ad.document.Document
import java.util.UUID

interface WorkflowService {
    suspend fun createWorkflow(command: CreateWorkflowCommand): Either<WorkflowError, WorkflowNode>
    suspend fun updateWorkflow(id: UUID, command: UpdateWorkflowCommand): Either<WorkflowError, WorkflowNode>
    suspend fun validateWorkflow(id: UUID): Either<WorkflowError, ValidationResult>
    suspend fun executeWorkflow(id: UUID, context: WorkflowContext): Either<WorkflowError, WorkflowResult>
}

data class CreateWorkflowCommand(
    val displayName: DisplayName,
    val description: Description? = null,
    val nodes: List<CreateNodeCommand>,
    val transitions: List<CreateTransitionCommand>
)

data class UpdateWorkflowCommand(
    val displayName: DisplayName? = null,
    val description: Description? = null,
    val nodes: List<UpdateNodeCommand>? = null,
    val transitions: List<UpdateTransitionCommand>? = null
)

data class ValidationResult(
    val valid: Boolean,
    val errors: List<ValidationError> = emptyList()
)

data class WorkflowContext(
    val document: Document,
    val attributes: Map<String, Any> = emptyMap(),
    val user: String
)

data class WorkflowResult(
    val completed: Boolean,
    val currentNode: UUID,
    val nextNodes: List<UUID>,
    val attributes: Map<String, Any>
)
