package org.blackerp.application.usecases.workflow

import org.blackerp.domain.ad.workflow.*
import org.springframework.stereotype.Service
import arrow.core.Either

@Service
class CreateWorkflowUseCase(
    private val workflowOperations: WorkflowOperations
) {
    suspend fun execute(command: CreateWorkflowCommand): Either<WorkflowError, WorkflowNode> =
        command.toNode().flatMap { node ->
            workflowOperations.save(node)
        }
}

data class CreateWorkflowCommand(
    val displayName: String,
    val description: String?,
    val type: NodeType,
    val action: NodeAction?,
    val transitions: List<CreateTransitionCommand>
)

data class CreateTransitionCommand(
    val targetNodeId: UUID,
    val condition: String?
)
