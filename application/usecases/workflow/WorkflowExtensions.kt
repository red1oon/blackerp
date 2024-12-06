package org.blackerp.application.usecases.workflow

import org.blackerp.domain.core.ad.workflow.*
import org.blackerp.domain.core.metadata.*
import org.blackerp.domain.core.values.*
import org.blackerp.application.api.process.CreateWorkflowCommand
import arrow.core.*
import java.util.UUID

suspend fun CreateWorkflowCommand.toNode(): Either<WorkflowError, WorkflowNode> {
    val metadata = EntityMetadata(
        id = UUID.randomUUID().toString(),
        audit = AuditInfo(createdBy = "system", updatedBy = "system")
    )

    return DisplayName.create(displayName)
        .mapLeft { WorkflowError.ValidationFailed(it.message) }
        .flatMap { displayName ->
            val desc = description?.let {
                Description.create(it).fold(
                    { WorkflowError.ValidationFailed(it.message).left() },
                    { it.right() }
                )
            } ?: null.right()

            desc.map { description ->
                WorkflowNode(
                    metadata = metadata,
                    displayName = displayName,
                    description = description,
                    type = type,
                    action = action,
                    transitions = transitions.map { t ->
                        WorkflowTransition(
                            id = UUID.randomUUID(),
                            sourceNode = UUID.randomUUID(),
                            targetNode = t.targetNodeId,
                            condition = t.condition?.let { TransitionCondition(it, "Auto-generated") }
                        )
                    }
                )
            }
        }
}
