package org.blackerp.application.api.controllers

import org.springframework.web.bind.annotation.*
import org.springframework.http.ResponseEntity
import org.blackerp.domain.core.ad.process.*
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
