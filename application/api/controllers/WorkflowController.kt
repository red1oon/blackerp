package org.blackerp.application.api.controllers

import org.blackerp.application.usecases.workflow.*
import org.springframework.web.bind.annotation.*
import org.springframework.http.ResponseEntity

@RestController
@RequestMapping("/api/workflows")
class WorkflowController(
    private val createWorkflowUseCase: CreateWorkflowUseCase
) {
    @PostMapping
    suspend fun createWorkflow(@RequestBody command: CreateWorkflowCommand): ResponseEntity<Any> =
        createWorkflowUseCase.execute(command).fold(
            { error -> ResponseEntity.badRequest().body(error) },
            { workflow -> ResponseEntity.ok(workflow) }
        )
}
