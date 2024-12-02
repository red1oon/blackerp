package org.blackerp.application.api.controllers

import org.blackerp.application.usecases.document.*
import org.springframework.web.bind.annotation.*
import org.springframework.http.ResponseEntity

@RestController
@RequestMapping("/api/documents")
class DocumentController(
    private val createDocumentUseCase: CreateDocumentUseCase
) {
    @PostMapping
    suspend fun createDocument(@RequestBody command: CreateDocumentCommand): ResponseEntity<Any> =
        createDocumentUseCase.execute(command).fold(
            { error -> ResponseEntity.badRequest().body(error) },
            { document -> ResponseEntity.ok(document) }
        )
}
