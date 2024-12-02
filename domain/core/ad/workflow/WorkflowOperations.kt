package org.blackerp.domain.ad.workflow

import arrow.core.Either
import java.util.UUID

interface WorkflowOperations {
    suspend fun save(node: WorkflowNode): Either<WorkflowError, WorkflowNode>
    suspend fun findById(id: UUID): Either<WorkflowError, WorkflowNode?>
    suspend fun delete(id: UUID): Either<WorkflowError, Unit>
    suspend fun validateWorkflow(nodes: List<WorkflowNode>): Either<WorkflowError, Unit>
}
