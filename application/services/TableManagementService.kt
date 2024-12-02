package org.blackerp.application.services

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.annotation.Propagation
import org.blackerp.domain.table.*
import org.blackerp.domain.core.error.TableError
import org.blackerp.domain.core.error.DomainError
import arrow.core.Either
import arrow.core.right
import arrow.core.left
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import java.util.UUID

@Service
class TableManagementService(
    private val tableOperations: TableOperations,
    private val columnOperations: ColumnOperations,
    private val tableValidator: TableValidator,
    private val tableMetrics: TableMetrics,
    private val metadataService: ADMetadataService
) {
    private val logger = LoggerFactory.getLogger(TableManagementService::class.java)

    @Transactional(rollbackFor = [Exception::class])
    suspend fun createTable(command: CreateTableCommand): Either<DomainError, ADTable> {
        val correlationId = UUID.randomUUID().toString()
        MDC.put("correlationId", correlationId)
        logger.info("Starting table creation with correlationId: $correlationId")

        return tableMetrics.timeCreateOperation {
            try {
                tableValidator.validateCreate(command)
                    .flatMap { validatedCommand -> 
                        metadataService.generateMetadata(command.name)
                            .flatMap { metadata ->
                                val table = ADTable.create(
                                    metadata = metadata,
                                    name = validatedCommand.name,
                                    displayName = validatedCommand.displayName,
                                    description = validatedCommand.description,
                                    accessLevel = validatedCommand.accessLevel,
                                    columns = validatedCommand.columns
                                )
                                saveTableWithColumns(table)
                            }
                    }
                    .also { 
                        tableMetrics.incrementCreateCounter()
                        logger.info("Table creation completed successfully: ${command.name}")
                    }
            } catch (e: Exception) {
                logger.error("Failed to create table: ${command.name}", e)
                TableError.DatabaseError(
                    message = "Failed to create table: ${e.message}",
                    sqlState = null,
                    errorCode = null
                ).left()
            } finally {
                MDC.remove("correlationId")
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private suspend fun saveTableWithColumns(table: ADTable): Either<DomainError, ADTable> =
        tableOperations.save(table)
            .flatMap { savedTable ->
                saveColumns(savedTable.id, table.columns)
                    .map { savedTable }
            }

    private suspend fun saveColumns(
        tableId: UUID, 
        columns: List<ColumnDefinition>
    ): Either<DomainError, List<ColumnDefinition>> {
        columns.forEach { column ->
            columnOperations.save(tableId, column)
                .fold(
                    { error -> return error.left() },
                    { /* continue with next column */ }
                )
        }
        return columns.right()
    }
}
