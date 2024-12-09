package org.blackerp.domain.core.workflow.tracking

import java.time.Instant
import java.util.UUID
import org.blackerp.domain.core.DomainEntity
import org.blackerp.domain.core.shared.EntityMetadata

// Record of state transitions
data class TransitionRecord(
    val fromNodeId: UUID,
    val toNodeId: UUID,
    val timestamp: Instant = Instant.now(),
    val actor: String,
    val attributes: Map<String, Any> = emptyMap()
)

// Domain interface for workflow execution tracking
interface WorkflowExecution : DomainEntity {
    val workflowId: UUID
    val documentId: UUID
    val startTime: Instant
    val endTime: Instant?
    val currentNodeId: UUID? 
    val status: ExecutionStatus
    val transitions: List<TransitionRecord>
}

// Concrete implementation for workflow execution tracking
data class WorkflowExecutionImpl(
    val uuid: UUID = UUID.randomUUID(),
    val metadata: EntityMetadata,
    override val workflowId: UUID,
    override val documentId: UUID,
    override val startTime: Instant,
    override val endTime: Instant? = null,
    override val currentNodeId: UUID? = null,
    override val status: ExecutionStatus = ExecutionStatus.PENDING,
    override val transitions: List<TransitionRecord> = emptyList()
) : WorkflowExecution {
    override val id: String
        get() = uuid.toString()
}
