package org.blackerp.application.services

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.blackerp.domain.ad.process.*
import arrow.core.Either
import arrow.core.right
import arrow.core.left
import kotlinx.coroutines.flow.Flow
import org.slf4j.LoggerFactory
import java.util.UUID
import java.time.Instant

@Service
class ProcessService(
    private val processRepository: ProcessRepository,
    private val processExecutor: ProcessExecutor,
    private val metadataService: ADMetadataService
) : ProcessOperations {
    private val logger = LoggerFactory.getLogger(ProcessService::class.java)

    @Transactional
    override suspend fun save(process: ADProcess): Either<ProcessError, ADProcess> {
        logger.debug("Saving process: ${process.displayName}")
        return validateProcess(process).fold(
            { error -> error.left() },
            { validProcess -> processRepository.save(validProcess) }
        )
    }

    override suspend fun findById(id: UUID): Either<ProcessError, ADProcess?> {
        logger.debug("Finding process by ID: $id")
        return processRepository.findById(id)
    }

    override suspend fun search(
        query: String,
        pageSize: Int,
        page: Int
    ): Flow<ADProcess> {
        logger.debug("Searching processes with query: $query")
        return processRepository.search(query, pageSize, page)
    }

    @Transactional
    override suspend fun delete(id: UUID): Either<ProcessError, Unit> {
        logger.debug("Deleting process: $id")
        return findById(id).fold(
            { error -> error.left() },
            { process ->
                if (process == null) {
                    ProcessError.NotFound(id).left()
                } else {
                    processRepository.delete(id)
                }
            }
        )
    }

    override suspend fun execute(
        id: UUID,
        parameters: Map<String, Any>,
        async: Boolean
    ): Either<ProcessError, ProcessResult> {
        logger.debug("Executing process: $id")
        val startTime = Instant.now()
        
        return validateParameters(id, parameters).fold(
            { error -> error.left() },
            { validatedParams ->
                processExecutor.execute(id, validatedParams, async).also { result ->
                    result.fold(
                        { error -> logger.error("Process execution failed: $error") },
                        { success -> 
                            logger.info("Process completed successfully: ${success.message}")
                            recordExecution(id, startTime, Instant.now(), success)
                        }
                    )
                }
            }
        )
    }

    override suspend fun schedule(
        id: UUID,
        schedule: ProcessSchedule
    ): Either<ProcessError, ADProcess> {
        logger.debug("Scheduling process: $id")
        return findById(id).fold(
            { error -> error.left() },
            { process ->
                if (process == null) {
                    ProcessError.NotFound(id).left()
                } else {
                    validateSchedule(schedule).fold(
                        { error -> error.left() },
                        { validSchedule ->
                            process.copy(schedule = validSchedule)
                                .let { updatedProcess -> save(updatedProcess) }
                        }
                    )
                }
            }
        )
    }

    override suspend fun getExecutionHistory(
        id: UUID,
        pageSize: Int,
        page: Int
    ): Flow<ProcessExecution> {
        logger.debug("Getting execution history for process: $id")
        return processRepository.getExecutionHistory(id, pageSize, page)
    }

    override suspend fun validateParameters(
        id: UUID,
        parameters: Map<String, Any>
    ): Either<ProcessError, Map<String, List<String>>> {
        logger.debug("Validating parameters for process: $id")
        return findById(id).fold(
            { error -> error.left() },
            { process ->
                if (process == null) {
                    ProcessError.NotFound(id).left()
                } else {
                    validateParametersAgainstDefinition(process.parameters, parameters)
                }
            }
        )
    }

    private fun validateProcess(process: ADProcess): Either<ProcessError, ADProcess> {
        // Implementation for process validation
        return process.right()
    }

    private fun validateSchedule(schedule: ProcessSchedule): Either<ProcessError, ProcessSchedule> {
        // Implementation for schedule validation
        return schedule.right()
    }

    private fun validateParametersAgainstDefinition(
        definition: List<ProcessParameter>,
        parameters: Map<String, Any>
    ): Either<ProcessError, Map<String, List<String>>> {
        // Implementation for parameter validation
        return emptyMap<String, List<String>>().right()
    }

    private suspend fun recordExecution(
        processId: UUID,
        startTime: Instant,
        endTime: Instant,
        result: ProcessResult
    ) {
        // Implementation for recording execution history
    }
}
