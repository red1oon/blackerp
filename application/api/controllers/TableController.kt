package org.blackerp.application.api.controllers

import org.springframework.web.bind.annotation.*
import org.springframework.http.MediaType
import jakarta.validation.Valid
import org.blackerp.domain.core.ad.table.TableOperations
import org.blackerp.application.table.CreateTableUseCase
import org.blackerp.application.api.mappers.TableMapper
import org.blackerp.application.api.dto.responses.TablesResponse
import org.blackerp.application.api.dto.responses.TableResponse
import org.blackerp.application.api.dto.requests.CreateTableRequest
import org.slf4j.LoggerFactory

@RestController
@RequestMapping("/api/tables")
class TableController(
    private val tableOperations: TableOperations,
    private val createTableUseCase: CreateTableUseCase,
    private val tableMapper: TableMapper
) {
    private val logger = LoggerFactory.getLogger(TableController::class.java)

    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun getTables(): TablesResponse {
        logger.debug("Getting all tables")
        val tables = tableOperations.findAll().getOrNull() ?: emptyList()
        return TablesResponse(tables = tables.map { tableMapper.toResponse(it) })
    }

    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun createTable(@Valid @RequestBody request: CreateTableRequest): TableResponse {
        logger.debug("Received create table request: $request")
        val command = tableMapper.toCommand(request)
        logger.debug("Created command: $command")
        
        val table = createTableUseCase.execute(command).fold(
            { error -> 
                logger.error("Failed to create table: $error")
                throw RuntimeException(error.message)
            },
            { it }
        )
        
        logger.debug("Successfully created table: ${table.name}")
        return tableMapper.toResponse(table)
    }
}