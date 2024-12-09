package org.blackerp.application.services.workflow

import org.springframework.stereotype.Component
import java.util.UUID
import java.time.Instant
import org.slf4j.LoggerFactory

@Component
class WorkflowExecutionTracker {
    private val logger = LoggerFactory.getLogger(WorkflowExecutionTracker::class.java)
    
    // In-memory tracking for POC - replace with persistence
    private val activeExecutions = mutableMapOf<UUID, WorkflowExecutionState>()

    fun startExecution(workflowId: UUID, document: UUID) {
        val executionId = UUID.randomUUID()
        activeExecutions[executionId] = WorkflowExecutionState(
            id = executionId,
            workflowId = workflowId,
            documentId = document,
            startTime = Instant.now(),
            status = ExecutionStatus.RUNNING
        )
        logger.info("Started workflow execution: {} for document: {}", executionId, document)
    }

    fun updateExecutionStatus(executionId: UUID, status: ExecutionStatus) {
        activeExecutions[executionId]?.let { state ->
            activeExecutions[executionId] = state.copy(
                status = status,
                endTime = if (status.isTerminal()) Instant.now() else null
            )
            logger.info("Updated execution status: {}-> {}", executionId, status)
        }
    }

    fun getExecutionState(executionId: UUID): WorkflowExecutionState? = activeExecutions[executionId]
}

data class WorkflowExecutionState(
    val id: UUID,
    val workflowId: UUID,
    val documentId: UUID,
    val startTime: Instant,
    val endTime: Instant? = null,
    val status: ExecutionStatus,
    val currentNode: UUID? = null,
    val attributes: Map<String, Any> = emptyMap()
)

enum class ExecutionStatus {
    RUNNING,
    COMPLETED,
    FAILED,
    CANCELLED;
    
    fun isTerminal() = this != RUNNING
}
