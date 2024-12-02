#!/bin/bash

# 1. First, create a service layer with proper transaction boundaries
cat > application/services/TableManagementService.kt << 'EOL'
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
EOL

# 2. Create metrics infrastructure
cat > application/services/TableMetrics.kt << 'EOL'
package org.blackerp.application.services

import org.springframework.stereotype.Component
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Timer
import org.slf4j.LoggerFactory

@Component
class TableMetrics(private val meterRegistry: MeterRegistry) {
    private val logger = LoggerFactory.getLogger(TableMetrics::class.java)

    private val createTableCounter = meterRegistry.counter("table.create.count")
    private val modifyTableCounter = meterRegistry.counter("table.modify.count")
    private val deleteTableCounter = meterRegistry.counter("table.delete.count")
    private val createTableTimer = meterRegistry.timer("table.create.duration")
    private val errorCounter = meterRegistry.counter("table.error.count")

    fun incrementCreateCounter() {
        createTableCounter.increment()
        logger.debug("Table creation counter incremented")
    }

    fun incrementModifyCounter() {
        modifyTableCounter.increment()
        logger.debug("Table modification counter incremented")
    }

    fun incrementDeleteCounter() {
        deleteTableCounter.increment()
        logger.debug("Table deletion counter incremented")
    }

    fun incrementErrorCounter() {
        errorCounter.increment()
        logger.debug("Error counter incremented")
    }

    fun <T> timeCreateOperation(block: () -> T): T {
        return createTableTimer.record<T> {
            try {
                block()
            } catch (e: Exception) {
                incrementErrorCounter()
                throw e
            }
        }
    }
}
EOL

# 3. Update health checks
cat > infrastructure/integration/adapters/TableHealthIndicator.kt << 'EOL'
package org.blackerp.infrastructure.integration.adapters

import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.health.HealthIndicator
import org.springframework.stereotype.Component
import org.springframework.jdbc.core.JdbcTemplate
import org.slf4j.LoggerFactory

@Component
class TableHealthIndicator(
    private val jdbcTemplate: JdbcTemplate
) : HealthIndicator {
    private val logger = LoggerFactory.getLogger(TableHealthIndicator::class.java)

    override fun health(): Health {
        return try {
            val tableCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM ad_table",
                Int::class.java
            ) ?: 0

            val recentTablesCount = jdbcTemplate.queryForObject("""
                SELECT COUNT(*) FROM ad_table 
                WHERE created > now() - interval '24 hours'
            """, Int::class.java) ?: 0

            Health.up()
                .withDetail("totalTables", tableCount)
                .withDetail("tablesCreatedLast24h", recentTablesCount)
                .build()
        } catch (e: Exception) {
            logger.error("Health check failed", e)
            Health.down()
                .withException(e)
                .build()
        }
    }
}
EOL

echo "Service layer transaction and metrics implementation completed."