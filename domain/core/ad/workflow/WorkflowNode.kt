package org.blackerp.domain.core.ad.workflow

import org.blackerp.domain.core.EntityMetadata
import org.blackerp.domain.core.ad.ADObject
import org.blackerp.domain.core.values.DisplayName
import org.blackerp.domain.core.values.Description
import arrow.core.Either
import arrow.core.right
import java.util.UUID

data class WorkflowNode(
    override val metadata: EntityMetadata,
    val id: UUID,
    override val displayName: DisplayName,
    override val description: Description?,
    val type: NodeType,
    val action: NodeAction?,
    val transitions: List<WorkflowTransition>
) : ADObject {
    companion object {
        fun create(params: CreateNodeParams): Either<WorkflowError, WorkflowNode> =
            WorkflowNode(
                metadata = params.metadata,
                id = params.id,
                displayName = params.displayName,
                description = params.description,
                type = params.type,
                action = params.action,
                transitions = params.transitions
            ).right()
    }
}
