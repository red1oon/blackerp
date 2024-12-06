package org.blackerp.domain.events

import java.util.UUID
import org.blackerp.domain.core.ad.workflow.NodeType
import org.blackerp.domain.core.ad.workflow.WorkflowNode

sealed class WorkflowEvent : DomainEvent {
    data class NodeCreated(
        override val metadata: EventMetadata,
        val node: WorkflowNode
    ) : WorkflowEvent()

    data class NodeUpdated(
        override val metadata: EventMetadata,
        val nodeId: UUID,
        val changes: Map<String, ChangePair>
    ) : WorkflowEvent()

    data class NodeDeleted(
        override val metadata: EventMetadata,
        val nodeId: UUID,
        val nodeName: String
    ) : WorkflowEvent()
}
