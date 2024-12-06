package org.blackerp.domain.core.ad.workflow

import org.blackerp.domain.core.ad.base.ADObject
import org.blackerp.domain.core.values.DisplayName
import org.blackerp.domain.core.values.Description
import org.blackerp.domain.core.metadata.EntityMetadata
import arrow.core.Either
import arrow.core.right
import java.util.UUID

data class WorkflowNode(
    override val metadata: EntityMetadata,
    private val uuid: UUID = UUID.randomUUID(),
    override val displayName: DisplayName,
    override val description: Description?,
    val type: NodeType,
    val action: NodeAction?,
    val transitions: List<WorkflowTransition>
) : ADObject {
    override val id: String get() = uuid.toString()
}

data class CreateNodeParams(
    val metadata: EntityMetadata,
    val displayName: DisplayName,
    val description: Description?,
    val type: NodeType,
    val action: NodeAction?,
    val transitions: List<WorkflowTransition>
)
