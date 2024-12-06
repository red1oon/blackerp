package org.blackerp.api.controllers

import org.springframework.web.bind.annotation.*
import org.blackerp.domain.core.ad.document.*
import org.springframework.http.ResponseEntity
import java.util.UUID

@RestController
@RequestMapping("/api/documents")
class DocumentController(
    private val documentService: DocumentOperations
) {
    @GetMapping("/{id}/history")
    suspend fun getHistory(
        @PathVariable id: UUID
    ): ResponseEntity<Any> =
        documentService.findById(id)
            .fold(
                { ResponseEntity.badRequest().body(it) },
                { ResponseEntity.ok(it) }
            )
}
