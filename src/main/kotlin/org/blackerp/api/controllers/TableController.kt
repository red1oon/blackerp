// File: src/main/kotlin/org/blackerp/api/controllers/TableController.kt
package org.blackerp.api.controllers

import org.springframework.web.bind.annotation.*
import org.springframework.http.MediaType
import jakarta.validation.Valid
import org.blackerp.domain.table.TableOperations
import org.blackerp.application.table.CreateTableUseCase
import org.blackerp.api.mappers.TableMapper
import org.blackerp.api.dto.response.TablesResponse
import org.blackerp.api.dto.response.TableResponse
import org.blackerp.api.dto.request.CreateTableRequest

@RestController
@RequestMapping("/api/tables")
class TableController(
    private val tableOperations: TableOperations,
    private val createTableUseCase: CreateTableUseCase,
    private val tableMapper: TableMapper
) {
    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun getTables(): TablesResponse {
        val tables = tableOperations.findAll().getOrNull() ?: emptyList()
        return TablesResponse(tables = tables.map { tableMapper.toResponse(it) })
    }

    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun createTable(@Valid @RequestBody request: CreateTableRequest): TableResponse {
        val command = tableMapper.toCommand(request)
        val table = createTableUseCase.execute(command).getOrNull() 
            ?: throw RuntimeException("Failed to create table")
        return tableMapper.toResponse(table)
    }
}