package org.blackerp.application.services

import org.springframework.stereotype.Service
import org.blackerp.domain.core.ad.workflow.*
import arrow.core.Either
import arrow.core.right
import arrow.core.left
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.slf4j.LoggerFactory
import java.util.UUID

@Service
class WorkflowService(
    private val workflowOperations: WorkflowOperations
) : WorkflowOperations {
    private val logger = LoggerFactory.getLogger(WorkflowService::class.java)

    override suspend fun save(node: WorkflowNode): Either<WorkflowError, WorkflowNode> =
        workflowOperations.save(node)

    override suspend fun findById(id: UUID): Either<WorkflowError, WorkflowNode?> =
        workflowOperations.findById(id)

    override suspend fun delete(id: UUID): Either<WorkflowError, Unit> =
        workflowOperations.delete(id)

    override suspend fun validateWorkflow(nodes: List<WorkflowNode>): Either<WorkflowError, Unit> =
        validateWorkflowStructure(nodes)

    private fun validateWorkflowStructure(nodes: List<WorkflowNode>): Either<WorkflowError, Unit> {
        // Basic validation rules
        if (nodes.isEmpty()) {
            return WorkflowError.ValidationFailed("Workflow must contain at least one node").left()
        }

        // Check for start and end nodes
        val startNodes = nodes.filter { it.type == NodeType.START }
        val endNodes = nodes.filter { it.type == NodeType.END }

        if (startNodes.isEmpty()) {
            return WorkflowError.ValidationFailed("Workflow must have a start node").left()
        }

        if (endNodes.isEmpty()) {
            return WorkflowError.ValidationFailed("Workflow must have an end node").left()
        }

        return Unit.right()
    }
}
