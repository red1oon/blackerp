package org.blackerp.infrastructure.workflow.tracking

import org.springframework.stereotype.Component
import org.blackerp.domain.core.workflow.tracking.*
import org.blackerp.domain.core.shared.EntityMetadata
import org.blackerp.domain.core.metadata.AuditInfo
import java.util.UUID
import java.time.Instant
import org.slf4j.LoggerFactory

@Component
class WorkflowExecutionTrackerImpl : WorkflowExecutionTracker {
    private val logger = LoggerFactory.getLogger(WorkflowExecutionTrackerImpl::class.java)
    
    // In-memory storage for POC
    private val executions = mutableMapOf<UUID, WorkflowExecution>()

    override suspend fun startExecution(workflowId: UUID, documentId: UUID): WorkflowExecution {
        val execution = WorkflowExecution(
            metadata = EntityMetadata(
                id = UUID.randomUUID().toString(),
                audit = AuditInfo(createdBy = "system", updatedBy = "system")
            ),
            workflowId = workflowId,
            documentId = documentId,
            startTime = Instant.now()
        )
        executions[documentId] = execution
        logger.info("Started workflow execution for document: $documentId")
        return execution
    }

    override suspend fun updateExecution(execution: WorkflowExecution): WorkflowExecution {
        executions[execution.documentId] = execution
        logger.info("Updated workflow execution for document: ${execution.documentId}, status: ${execution.status}")
        return execution
    }

    override suspend fun getExecution(documentId: UUID): WorkflowExecution? {
        return executions[documentId]
    }

    override suspend fun getExecutions(workflowId: UUID): List<WorkflowExecution> {
        return executions.values.filter { it.workflowId == workflowId }
    }
}
