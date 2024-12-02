package org.blackerp.application.api.controllers

import org.blackerp.application.usecases.process.*
import org.springframework.web.bind.annotation.*
import org.springframework.http.ResponseEntity

@RestController
@RequestMapping("/api/processes")
class ProcessController(
    private val createProcessUseCase: CreateProcessUseCase
) {
    @PostMapping
    suspend fun createProcess(@RequestBody command: CreateProcessCommand): ResponseEntity<Any> =
        createProcessUseCase.execute(command).fold(
            { error -> ResponseEntity.badRequest().body(error) },
            { process -> ResponseEntity.ok(process) }
        )

    @PostMapping("/{id}/execute")
    suspend fun executeProcess(
        @PathVariable id: UUID,
        @RequestBody parameters: Map<String, Any>
    ): ResponseEntity<Any> =
        processOperations.execute(id, parameters).fold(
            { error -> ResponseEntity.badRequest().body(error) },
            { result -> ResponseEntity.ok(result) }
        )
}
