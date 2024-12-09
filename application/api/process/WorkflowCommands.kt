package org.blackerp.application.api.process

import org.blackerp.domain.core.ad.workflow.NodeType
import org.blackerp.domain.core.ad.workflow.NodeAction
import java.util.UUID

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
