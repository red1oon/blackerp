package org.blackerp.application.services.table

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.blackerp.domain.core.ad.table.*
import org.blackerp.domain.core.error.TableError
import org.blackerp.domain.events.TableEvent
import org.blackerp.domain.events.DomainEvent
import org.blackerp.domain.events.EventMetadata
import org.blackerp.infrastructure.events.publishers.DomainEventPublisher
import org.blackerp.application.services.core.base.BaseDomainServiceImpl
import org.blackerp.application.services.core.base.SearchCriteria
import org.blackerp.domain.core.metadata.*
import arrow.core.*
import kotlinx.coroutines.flow.*
import java.util.UUID

/**
 * Table management service implementing AD-centric principles.
 * All table operations are driven by AD metadata definitions.
 */
@Service
class TableManagementServiceImpl(
    eventPublisher: DomainEventPublisher,
    private val tableOperations: TableOperations,
    private val tableValidator: TableValidator,
    private val metadataService: ADMetadataService
) : BaseDomainServiceImpl<ADTable, TableError>(eventPublisher) {

    override suspend fun validateCreate(entity: ADTable): Either<TableError, ADTable> =
        tableValidator.validateCreate(CreateTableCommand(
            name = entity.name,
            displayName = entity.displayName,
            description = entity.description,
            accessLevel = entity.accessLevel,
            columns = entity.columns.map { column ->
                CreateColumnCommand(
                    name = column.name,
                    displayName = column.name,
                    dataType = column.dataType,
                    mandatory = false
                )
            }
        )).map { entity }

    override suspend fun validateUpdate(id: UUID, entity: ADTable): Either<TableError, ADTable> =
        tableValidator.validateUpdate(
            id,
            UpdateTableCommand(
                displayName = entity.displayName,
                description = entity.description,
                columns = entity.columns.map { column ->
                    UpdateColumnCommand(
                        name = column.name,
                        displayName = column.name,
                        description = null,
                        mandatory = false
                    )
                }
            )
        ).map { entity }

    override suspend fun executeCreate(entity: ADTable): Either<TableError, ADTable> =
        metadataService.generateMetadata("table")
            .map { metadata -> entity.copy(metadata = metadata) }
            .flatMap { tableOperations.save(it) }

    override suspend fun executeUpdate(id: UUID, entity: ADTable): Either<TableError, ADTable> =
        executeFindById(id).flatMap { existing ->
            existing?.let { current ->
                metadataService.incrementVersion(current.metadata)
                    .map { metadata -> entity.copy(metadata = metadata) }
                    .flatMap { tableOperations.save(it) }
            } ?: TableError.ValidationError(
                message = "Table not found",
                violations = listOf(TableError.Violation("id", "No table found with this ID", id))
            ).left()
        }

    override suspend fun executeFindById(id: UUID): Either<TableError, ADTable?> =
        tableOperations.findById(id)

    override suspend fun executeDelete(id: UUID): Either<TableError, Unit> =
        tableOperations.delete(id)

    override suspend fun search(criteria: SearchCriteria): Flow<ADTable> = flow {
        tableOperations.findAll().fold(
            { error -> throw RuntimeException(error.message) },
            { tables -> emitAll(flowOf(*tables.toTypedArray())) }
        )
    }

    override fun handleUnexpectedError(e: Exception): TableError =
        TableError.DatabaseError(
            message = "Unexpected error: ${e.message}",
            sqlState = null,
            errorCode = null
        )

    override fun createCreatedEvent(entity: ADTable): DomainEvent =
        TableEvent.TableCreated(
            metadata = EventMetadata(user = "system"), // TODO: Get from security context
            tableId = UUID.fromString(entity.id),
            name = entity.name.value,
            displayName = entity.displayName.value
        )

    override fun createUpdatedEvent(entity: ADTable): DomainEvent =
        TableEvent.TableModified(
            metadata = EventMetadata(user = "system"), // TODO: Get from security context
            tableId = UUID.fromString(entity.id),
            changes = mapOf() // TODO: Track actual changes
        )

    override fun createDeletedEvent(id: UUID): DomainEvent =
        TableEvent.TableDeleted(
            metadata = EventMetadata(user = "system"), // TODO: Get from security context
            tableId = id
        )
}
