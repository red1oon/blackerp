package org.blackerp.application.services.process

import org.springframework.stereotype.Service
import org.blackerp.domain.core.ad.process.*
import org.blackerp.infrastructure.process.executor.ProcessExecutorImpl
import org.blackerp.infrastructure.process.tracking.ProcessExecutionTracker
import org.blackerp.infrastructure.process.validation.ParameterValidator
import arrow.core.Either
import arrow.core.flatMap
import kotlinx.coroutines.flow.*
import java.util.UUID
import java.time.Instant
import org.slf4j.LoggerFactory

@Service
class ProcessServiceImpl(
    private val processOperations: ProcessOperations,
    private val processExecutor: ProcessExecutorImpl,
    private val executionTracker: ProcessExecutionTracker,
    private val parameterValidator: ParameterValidator
) : ProcessService {
    private val logger = LoggerFactory.getLogger(ProcessServiceImpl::class.java)

    override suspend fun execute(
        id: UUID,
        parameters: Map<String, Any>,
        async: Boolean
    ): Either<ProcessError, ProcessResult> {
        logger.debug("Executing process $id with parameters: $parameters")
        
        return processOperations.findById(id).flatMap { process ->
            process?.let { p ->
                // Validate parameters
                validateParameters(id, parameters).flatMap { validatedParams ->
                    // Execute process
                    processExecutor.execute(id, validatedParams, async).map { result ->
                        // Track execution
                        trackExecution(id, parameters, result)
                        result
                    }
                }
            } ?: ProcessError.NotFound(id).left()
        }
    }

    override suspend fun validateParameters(
        id: UUID,
        parameters: Map<String, Any>
    ): Either<ProcessError, Map<String, Any>> {
        return processOperations.findById(id).flatMap { process ->
            process?.let { p ->
                parameterValidator.validate(parameters, p.parameters)
            } ?: ProcessError.NotFound(id).left()
        }
    }

    private fun trackExecution(
        processId: UUID,
        parameters: Map<String, Any>,
        result: ProcessResult
    ) {
        val execution = ProcessExecution(
            processId = processId,
            startTime = Instant.now(),
            endTime = Instant.now(),
            status = if (result.success) ExecutionStatus.COMPLETED else ExecutionStatus.FAILED,
            parameters = parameters,
            result = result,
            user = "system" // TODO: Get from security context
        )
        executionTracker.trackExecution(processId, execution)
    }
}
