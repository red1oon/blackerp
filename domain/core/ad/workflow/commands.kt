// domain/core/ad/workflow/commands.kt
package org.blackerp.domain.core.ad.workflow

import java.util.UUID

data class CreateNodeCommand(
    val type: NodeType,
    val displayName: String,
    val description: String?,
    val action: NodeAction?
)

data class UpdateNodeCommand(
    val displayName: String?,
    val description: String?,
    val action: NodeAction?
)

data class CreateTransitionCommand(
    val fromNode: UUID,
    val toNode: UUID,
    val condition: String?
)

data class UpdateTransitionCommand(
    val condition: String?
)
