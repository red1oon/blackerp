package org.blackerp.domain.events

import java.util.UUID
import org.blackerp.domain.ad.workflow.NodeType
import org.blackerp.domain.ad.workflow.WorkflowNode

sealed class WorkflowEvent : DomainEvent {
    data class NodeCreated(
        override val metadata: EventMetadata,
        val nodeId: UUID,
        val type: NodeType
    ) : WorkflowEvent()

    data class NodeUpdated(
        override val metadata: EventMetadata,
        val nodeId: UUID,
        val previousVersion: Int,
        val newVersion: Int
    ) : WorkflowEvent()

    data class NodeDeleted(
        override val metadata: EventMetadata,
        val nodeId: UUID
    ) : WorkflowEvent()
}
