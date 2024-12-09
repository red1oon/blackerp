package org.blackerp.application.api.controllers

import org.springframework.web.bind.annotation.*
import org.springframework.http.ResponseEntity
import org.blackerp.domain.core.ad.document.*
import org.blackerp.application.services.DocumentLifecycleService
import java.util.UUID
import kotlinx.coroutines.runBlocking

@RestController
@RequestMapping("/api/documents")
class DocumentLifecycleController(
    private val lifecycleService: DocumentLifecycleService
) {
    @PostMapping("/{id}/status")
    suspend fun changeStatus(
        @PathVariable id: UUID,
        @RequestBody command: ChangeStatusCommand
    ): ResponseEntity<Document> = 
        lifecycleService.changeStatus(id, command)
            .fold(
                { error -> ResponseEntity.badRequest().build() },
                { document -> ResponseEntity.ok(document) }
            )

    @GetMapping("/{id}/history")
    suspend fun getHistory(
        @PathVariable id: UUID
    ): ResponseEntity<List<DocumentChange>> =
        lifecycleService.getHistory(id)
            .fold(
                { error -> ResponseEntity.badRequest().build() },
                { history -> ResponseEntity.ok(history) }
            )
}
