package org.blackerp.application.api.controllers

import org.springframework.web.bind.annotation.*
import org.springframework.http.ResponseEntity
import org.blackerp.domain.core.ad.process.*
import org.blackerp.application.services.ProcessService
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import java.util.UUID
import kotlinx.coroutines.flow.toList

@RestController
@RequestMapping("/api/processes")
class ProcessController(
    private val processService: ProcessService
) {
    private val logger = LoggerFactory.getLogger(ProcessController::class.java)

    @GetMapping("/{id}/history")
    suspend fun getProcessHistory(
        @PathVariable id: UUID,
        @RequestParam(defaultValue = "20") pageSize: Int,
        @RequestParam(defaultValue = "0") page: Int
    ): ResponseEntity<List<ProcessExecution>> {
        val executions = processService.getExecutionHistory(id, pageSize, page).toList()
        return ResponseEntity.ok(executions)
    }

    @PostMapping("/{id}/execute")
    suspend fun executeProcess(
        @PathVariable id: UUID,
        @RequestBody parameters: Map<String, Any>,
        @RequestParam(defaultValue = "false") async: Boolean
    ): ResponseEntity<ProcessResult> =
        processService.execute(id, parameters, async).fold(
            { error -> ResponseEntity.badRequest().build() },
            { result -> ResponseEntity.ok(result) }
        )

    @PostMapping("/{id}/schedule")
    suspend fun scheduleProcess(
        @PathVariable id: UUID,
        @Valid @RequestBody schedule: ProcessSchedule
    ): ResponseEntity<ADProcess> =
        processService.schedule(id, schedule).fold(
            { error -> ResponseEntity.badRequest().build() },
            { process -> ResponseEntity.ok(process) }
        )
}
