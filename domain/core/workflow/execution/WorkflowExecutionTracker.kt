package org.blackerp.domain.core.workflow.execution

import java.util.UUID
import java.time.Instant
import org.slf4j.LoggerFactory

data class WorkflowExecution(
    val id: UUID = UUID.randomUUID(),
    val documentId: UUID,
    val workflowId: UUID,
    val startTime: Instant,
    val endTime: Instant? = null,
    val currentState: String,
    val history: List<StateTransitionRecord> = emptyList()
)

data class StateTransitionRecord(
    val fromState: String,
    val toState: String,
    val timestamp: Instant,
    val actor: String,
    val parameters: Map<String, Any> = emptyMap()
)

interface WorkflowExecutionRepository {
    suspend fun save(execution: WorkflowExecution): WorkflowExecution
    suspend fun findByDocumentId(documentId: UUID): WorkflowExecution?
    suspend fun addTransition(
        executionId: UUID,
        transition: StateTransitionRecord
    ): WorkflowExecution
}
