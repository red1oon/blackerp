package org.blackerp.infrastructure.process.tracking

import org.springframework.stereotype.Component
import org.blackerp.domain.core.ad.process.*
import java.util.UUID
import java.time.Instant
import org.slf4j.LoggerFactory

@Component
class ProcessExecutionTracker {
    private val logger = LoggerFactory.getLogger(ProcessExecutionTracker::class.java)
    
    // In-memory tracking for POC
    private val executionHistory = mutableMapOf<UUID, ProcessExecution>()

    fun trackExecution(
        processId: UUID,
        execution: ProcessExecution
    ) {
        executionHistory[execution.id] = execution
        logger.info("Tracked execution: ${execution.id} for process: $processId")
    }

    fun getExecutionHistory(processId: UUID): List<ProcessExecution> =
        executionHistory.values
            .filter { it.processId == processId }
            .sortedByDescending { it.startTime }
}
