package org.blackerp.application.services.table

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.blackerp.domain.core.ad.table.*
import org.blackerp.domain.core.error.TableError
import org.blackerp.domain.core.metadata.*
import org.blackerp.application.services.metrics.TableMetrics
import arrow.core.*
import kotlinx.coroutines.flow.*
import java.util.UUID

@Service
class TableManagementService(
    private val tableOperations: TableOperations,
    private val tableValidator: TableValidator,
    private val tableMetrics: TableMetrics
) {
    @Transactional
    suspend fun createTable(command: CreateTableCommand): Either<TableError, ADTable> =
        tableValidator.validateCreate(command).flatMap { validatedCommand ->
            val table = ADTable(
                metadata = EntityMetadata(
                    id = UUID.randomUUID().toString(),
                    audit = AuditInfo(createdBy = "system", updatedBy = "system"),
                    version = VersionInfo()
                ),
                name = validatedCommand.name,
                displayName = validatedCommand.displayName,
                description = validatedCommand.description,
                accessLevel = validatedCommand.accessLevel,
                columns = emptyList()
            )
            tableOperations.save(table)
        }

    suspend fun findTables(): Flow<ADTable> = flow {
        tableOperations.findAll().fold(
            { emitAll(flowOf()) },
            { tables -> emitAll(flowOf(*tables.toTypedArray())) }
        )
    }
}
