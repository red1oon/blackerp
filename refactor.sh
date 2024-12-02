#!/bin/bash

# generate_process_management.sh
# Generates process management components building on reference system

echo "Generating process management components..."

# 1. Enhanced Process Domain Model
cat > domain/core/ad/process/ADProcess.kt << 'EOF'
package org.blackerp.domain.ad.process

import org.blackerp.domain.EntityMetadata
import org.blackerp.domain.ad.ADObject
import org.blackerp.domain.values.DisplayName
import org.blackerp.domain.values.Description
import arrow.core.Either
import arrow.core.right
import java.util.UUID

data class ADProcess(
    override val metadata: EntityMetadata,
    val id: UUID = UUID.randomUUID(),
    override val displayName: DisplayName,
    override val description: Description?,
    val type: ProcessType,
    val parameters: List<ProcessParameter>,
    val implementation: ProcessImplementation,
    val schedule: ProcessSchedule?,
    val accessLevel: AccessLevel = AccessLevel.ORGANIZATION,
    val version: Int = 1,
    val status: ProcessStatus = ProcessStatus.ACTIVE
) : ADObject {
    companion object {
        fun create(params: CreateProcessParams): Either<ProcessError, ADProcess> =
            ADProcess(
                metadata = params.metadata,
                displayName = params.displayName,
                description = params.description,
                type = params.type,
                parameters = params.parameters,
                implementation = params.implementation,
                schedule = params.schedule
            ).right()
    }
}

enum class ProcessType {
    REPORT,
    CALCULATION,
    SYNCHRONIZATION,
    WORKFLOW,
    DATA_IMPORT,
    DATA_EXPORT,
    CUSTOM
}

enum class ProcessStatus {
    DRAFT,
    ACTIVE,
    SUSPENDED,
    DEPRECATED
}

data class ProcessParameter(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val displayName: String,
    val description: String?,
    val parameterType: ParameterType,
    val referenceId: UUID?,
    val defaultValue: String?,
    val isMandatory: Boolean = false,
    val validationRule: String?
)

enum class ParameterType {
    STRING,
    NUMBER,
    DATE,
    BOOLEAN,
    REFERENCE,
    FILE
}

sealed interface ProcessImplementation {
    data class JavaClass(
        val className: String,
        val methodName: String = "execute"
    ) : ProcessImplementation
    
    data class DatabaseFunction(
        val functionName: String,
        val schema: String = "public"
    ) : ProcessImplementation
    
    data class Script(
        val language: String,
        val code: String,
        val version: String = "1.0"
    ) : ProcessImplementation
    
    data class RestEndpoint(
        val url: String,
        val method: String,
        val headers: Map<String, String> = emptyMap(),
        val bodyTemplate: String?
    ) : ProcessImplementation
}

data class ProcessSchedule(
    val cronExpression: String,
    val timezone: String = "UTC",
    val startDate: java.time.LocalDateTime? = null,
    val endDate: java.time.LocalDateTime? = null,
    val maxExecutions: Int? = null,
    val enabled: Boolean = true
)

sealed class ProcessError {
    data class ValidationFailed(val message: String) : ProcessError()
    data class ExecutionFailed(val message: String) : ProcessError()
    data class NotFound(val id: UUID) : ProcessError()
    data class InvalidSchedule(val message: String) : ProcessError()
    data class InvalidImplementation(val message: String) : ProcessError()
}

data class ProcessResult(
    val success: Boolean,
    val message: String?,
    val data: Map<String, Any>? = null,
    val logs: List<String> = emptyList(),
    val executionTime: Long? = null
)

enum class AccessLevel {
    SYSTEM,
    CLIENT,
    ORGANIZATION,
    CLIENT_ORGANIZATION
}
EOF

# 2. Process Operations Interface
cat > domain/core/ad/process/ProcessOperations.kt << 'EOF'
package org.blackerp.domain.ad.process

import arrow.core.Either
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface ProcessOperations {
    suspend fun save(process: ADProcess): Either<ProcessError, ADProcess>
    suspend fun findById(id: UUID): Either<ProcessError, ADProcess?>
    suspend fun search(query: String, pageSize: Int = 20, page: Int = 0): Flow<ADProcess>
    suspend fun delete(id: UUID): Either<ProcessError, Unit>
    suspend fun execute(
        id: UUID,
        parameters: Map<String, Any>,
        async: Boolean = false
    ): Either<ProcessError, ProcessResult>
    suspend fun schedule(
        id: UUID,
        schedule: ProcessSchedule
    ): Either<ProcessError, ADProcess>
    suspend fun getExecutionHistory(
        id: UUID,
        pageSize: Int = 20,
        page: Int = 0
    ): Flow<ProcessExecution>
    suspend fun validateParameters(
        id: UUID,
        parameters: Map<String, Any>
    ): Either<ProcessError, Map<String, List<String>>>
}

data class ProcessExecution(
    val id: UUID = UUID.randomUUID(),
    val processId: UUID,
    val startTime: java.time.Instant,
    val endTime: java.time.Instant?,
    val status: ExecutionStatus,
    val parameters: Map<String, Any>,
    val result: ProcessResult?,
    val user: String
)

enum class ExecutionStatus {
    QUEUED,
    RUNNING,
    COMPLETED,
    FAILED,
    CANCELLED
}
EOF

# 3. Process Service Implementation
cat > application/services/ProcessService.kt << 'EOF'
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
EOF

# 4. Process Controller
cat > application/api/controllers/ProcessController.kt << 'EOF'
package org.blackerp.application.api.controllers

import org.springframework.web.bind.annotation.*
import org.springframework.http.ResponseEntity
import org.blackerp.domain.ad.process.*
import org.blackerp.application.services.ProcessService
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import java.util.UUID

@RestController
@RequestMapping("/api/processes")
class ProcessController(
    private val processService: ProcessService
) {
    private val logger = LoggerFactory.getLogger(ProcessController::class.java)

    @PostMapping
    suspend fun createProcess(
        @Valid @RequestBody request: CreateProcessRequest
    ): ResponseEntity<ADProcess> {
        logger.debug("Creating process: ${request.displayName}")
        return processService.save(request.toDomain()).fold(
            { error -> ResponseEntity.badRequest().build() },
            { process -> ResponseEntity.ok(process) }
        )
    }

    @PostMapping("/{id}/execute")
    suspend fun executeProcess(
        @PathVariable id: UUID,
        @RequestBody parameters: Map<String, Any>,
        @RequestParam(defaultValue = "false") async: Boolean
    ): ResponseEntity<ProcessResult> {
        logger.debug("Executing process: $id")
        return processService.execute(id, parameters, async).fold(
            { error -> ResponseEntity.badRequest().build() },
            { result -> ResponseEntity.ok(result) }
        )
    }

    @PostMapping("/{id}/schedule")
    suspend fun scheduleProcess(
        @PathVariable id: UUID,
        @Valid @RequestBody schedule: ProcessSchedule
    ): ResponseEntity<ADProcess> {
        logger.debug("Scheduling process: $id")
        return processService.schedule(id, schedule).fold(
            { error -> ResponseEntity.badRequest().build() },
            { process -> ResponseEntity.ok(process) }
        )
    }

    @GetMapping("/{id}/history")
    suspend fun getProcessHistory(
        @PathVariable id: UUID,
        @RequestParam(defaultValue = "20") pageSize: Int,
        @RequestParam(defaultValue = "0") page: Int
    ): ResponseEntity<List<ProcessExecution>> {
        return processService.getExecutionHistory(id, pageSize, page)
            .collect { executions -> ResponseEntity.ok(executions) }
    }

    data class CreateProcessRequest(
        val displayName: String,
        val description: String?,
        val type: String,
        val parameters: List<ParameterRequest>,
        val implementation: ImplementationRequest,
        val schedule: ScheduleRequest?
    ) {
        fun toDomain(): ADProcess {
            // Implementation for conversion to domain object
            TODO("Implement conversion")
        }
    }

    data class ParameterRequest(
        val name: String,
        val displayName: String,
        val description: String?,
        val type: String,
        val referenceId: UUID?,
        val defaultValue: String?,
        val isMandatory: Boolean,
        val validationRule: String?
    )

    data class ImplementationRequest(
        val type: String,
        val config: Map<String, String>
    )

    data class ScheduleRequest(
        val cronExpression: String,
        val timezone: String?,
        val startDate: String?,
        val endDate: String?,
        val maxExecutions: Int?,
        val enabled: Boolean?
    )
}
EOF

echo "Process management components generated successfully!"