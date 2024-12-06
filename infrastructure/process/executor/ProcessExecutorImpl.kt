package org.blackerp.infrastructure.process.executor

import org.springframework.stereotype.Component
import org.blackerp.domain.core.ad.process.*
import org.blackerp.domain.core.error.ProcessError
import org.blackerp.infrastructure.process.tracking.ProcessExecutionTracker
import arrow.core.Either
import arrow.core.left
import arrow.core.right
import org.slf4j.LoggerFactory
import java.util.UUID
import java.time.Instant
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import org.springframework.transaction.annotation.Transactional

@Component
class ProcessExecutorImpl(
    private val processOperations: ProcessOperations,
    private val executionTracker: ProcessExecutionTracker
) {
    private val logger = LoggerFactory.getLogger(ProcessExecutorImpl::class.java)

    @Transactional
    suspend fun execute(
        processId: UUID,
        parameters: Map<String, Any>,
        async: Boolean = false
    ): Either<ProcessError, ProcessResult> = withContext(Dispatchers.IO) {
        try {
            logger.info("Executing process: $processId with parameters: $parameters")
            
            val executionId = UUID.randomUUID()
            val startTime = Instant.now()
            
            val result = executeProcess(processId, parameters)
            
            // Track execution
            trackExecution(
                processId = processId,
                executionId = executionId,
                parameters = parameters,
                result = result,
                startTime = startTime,
                endTime = Instant.now()
            )

            result.right()
        } catch (e: Exception) {
            logger.error("Process execution failed", e)
            ProcessError.ExecutionFailed(e.message ?: "Unknown error").left()
        }
    }

    private suspend fun executeProcess(
        processId: UUID,
        parameters: Map<String, Any>
    ): ProcessResult {
        // For POC, simulate process execution
        return ProcessResult(
            success = true,
            message = "Process executed successfully",
            data = mapOf(
                "processId" to processId.toString(),
                "executionTime" to Instant.now().toString()
            )
        )
    }

    private fun trackExecution(
        processId: UUID,
        executionId: UUID,
        parameters: Map<String, Any>,
        result: ProcessResult,
        startTime: Instant,
        endTime: Instant
    ) {
        val execution = ProcessExecution(
            id = executionId,
            processId = processId,
            startTime = startTime,
            endTime = endTime,
            status = if (result.success) ExecutionStatus.COMPLETED else ExecutionStatus.FAILED,
            parameters = parameters,
            result = result,
            user = "system" // TODO: Replace with actual user from security context
        )
        executionTracker.trackExecution(processId, execution)
        logger.debug("Tracked execution $executionId for process $processId")
    }
}
