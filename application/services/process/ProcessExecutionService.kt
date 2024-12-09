package org.blackerp.application.services.process

import org.springframework.stereotype.Service
import org.blackerp.domain.core.ad.process.*
import org.blackerp.infrastructure.process.tracking.ProcessExecutionTracker
import org.blackerp.infrastructure.process.executor.ProcessExecutorImpl
import arrow.core.Either
import kotlinx.coroutines.flow.*
import java.util.UUID
import org.slf4j.LoggerFactory

@Service
class ProcessExecutionService(
    private val processExecutor: ProcessExecutorImpl,
    private val executionTracker: ProcessExecutionTracker
) {
    private val logger = LoggerFactory.getLogger(ProcessExecutionService::class.java)

    suspend fun executeProcess(
        processId: UUID,
        parameters: Map<String, Any>,
        async: Boolean = false
    ): Either<ProcessError, ProcessResult> {
        logger.debug("Initiating process execution: $processId")
        return processExecutor.execute(processId, parameters, async)
    }

    fun getExecutionHistory(processId: UUID): List<ProcessExecution> {
        logger.debug("Fetching execution history for process: $processId")
        return executionTracker.getExecutionHistory(processId)
    }
}
