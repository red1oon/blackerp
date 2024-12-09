package org.blackerp.application.usecases.workflow

import org.springframework.stereotype.Service
import org.blackerp.domain.core.ad.workflow.*
import org.blackerp.application.api.process.CreateWorkflowCommand
import arrow.core.Either
import arrow.core.flatMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Service
class CreateWorkflowUseCase(
    private val workflowOperations: WorkflowOperations
) {
    suspend fun execute(command: CreateWorkflowCommand): Either<WorkflowError, WorkflowNode> {
        return withContext(Dispatchers.IO) {
            command.toNode().flatMap { node ->
                workflowOperations.save(node)
            }
        }
    }
}
