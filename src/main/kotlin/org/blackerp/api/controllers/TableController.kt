// File: src/main/kotlin/org/blackerp/api/controllers/TableController.kt
package org.blackerp.api.controllers

import org.springframework.web.bind.annotation.*
import org.blackerp.application.table.CreateTableUseCase
import org.blackerp.api.dto.CreateTableRequest
import org.blackerp.api.dto.TableResponse
import org.blackerp.api.mappers.TableMapper
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import java.util.UUID

@RestController
@RequestMapping("/api/tables")
class TableController(
    private val createTableUseCase: CreateTableUseCase,
    private val tableMapper: TableMapper
) {
    @PostMapping
    suspend fun createTable(
        @Valid @RequestBody request: CreateTableRequest
    ): ResponseEntity<TableResponse> =
        createTableUseCase
            .execute(tableMapper.toCommand(request))
            .fold(
                { throw it },
                { ResponseEntity.ok(tableMapper.toResponse(it)) }
            )
}