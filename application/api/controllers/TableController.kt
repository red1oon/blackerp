package org.blackerp.application.api.controllers

import org.springframework.web.bind.annotation.*
import org.springframework.http.ResponseEntity
import org.blackerp.application.api.dto.TableDTO
import org.blackerp.application.api.dto.requests.CreateTableRequest
import org.blackerp.application.services.table.TableManagementService
import org.blackerp.domain.core.ad.table.*
import org.blackerp.domain.core.values.*
import org.blackerp.domain.core.shared.ValidationError
import arrow.core.*
import jakarta.validation.Valid
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList

@RestController
@RequestMapping("/api/tables")
class TableController(private val tableService: TableManagementService) {
   @GetMapping
   suspend fun getTables(): ResponseEntity<List<TableDTO>> = 
       tableService.findTables()
           .map { TableDTO.fromDomain(it) }
           .toList()
           .let { ResponseEntity.ok(it) }

   @PostMapping
   suspend fun createTable(@Valid @RequestBody request: CreateTableRequest): ResponseEntity<TableDTO> = 
       Either.catch {
           TableName.create(request.name).orNull()?.let { tableName ->
               DisplayName.create(request.displayName).orNull()?.let { displayName ->
                   val description = request.description?.let { desc ->
                       Description.create(desc).orNull()
                   }
                   
                   CreateTableCommand(
                       name = tableName,
                       displayName = displayName,
                       description = description,
                       accessLevel = AccessLevel.valueOf(request.accessLevel.uppercase()),
                       columns = emptyList()
                   )
               }
           } ?: throw IllegalArgumentException("Invalid input")
       }.fold(
           { ResponseEntity.badRequest().build() },
           { command -> 
               tableService.createTable(command).fold(
                   { ResponseEntity.badRequest().build() },
                   { ResponseEntity.ok(TableDTO.fromDomain(it)) }
               )
           }
       )
}
