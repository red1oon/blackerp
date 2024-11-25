// File: src/main/kotlin/org/blackerp/application/table/CreateTableUseCase.kt
package org.blackerp.application.table

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import org.blackerp.domain.EntityMetadata
import org.blackerp.domain.table.ADTable
import org.blackerp.domain.table.TableError
import org.blackerp.domain.table.CreateTableParams
import org.blackerp.domain.table.CreateColumnParams
import org.blackerp.domain.table.ColumnDefinition
import org.blackerp.domain.table.TableOperations
import org.blackerp.domain.values.*
import org.blackerp.domain.event.DomainEvent
import org.blackerp.domain.event.TableCreated
import org.blackerp.domain.event.EventMetadata
import org.blackerp.infrastructure.event.EventPublisher
import org.blackerp.shared.ValidationError
import java.time.Instant
import java.util.UUID
import org.springframework.stereotype.Service

@Service
class CreateTableUseCase(
    private val operations: TableOperations,
    private val eventPublisher: EventPublisher
) {
    suspend fun execute(command: CreateTableCommand): Either<TableError, ADTable> {
        val errors = mutableListOf<ValidationError>()
        
        val name = TableName.create(command.name).fold(
            { errors.add(it); null },
            { it }
        )
        
        val displayName = DisplayName.create(command.displayName).fold(
            { errors.add(it); null },
            { it }
        )
        
        val description = command.description?.let { desc ->
            Description.create(desc).fold(
                { errors.add(it); null },
                { it }
            )
        }

        // If name or displayName is null, return validation errors immediately
        if (name == null || displayName == null) {
            return TableError.ValidationFailed(errors).left()
        }

        val metadata = EntityMetadata(
            createdBy = command.createdBy,
            updatedBy = command.createdBy
        )

        val columns = command.columns.mapNotNull { columnCommand ->
            ColumnDefinition.create(
                CreateColumnParams(
                    metadata = metadata,
                    name = ColumnName.create(columnCommand.name).getOrNull()!!,
                    displayName = DisplayName.create(columnCommand.displayName).getOrNull()!!, 
                    description = columnCommand.description?.let { Description.create(it).getOrNull() }, 
                    dataType = columnCommand.dataType,
                    length = columnCommand.length?.let { Length.create(it).getOrNull() },
                    precision = columnCommand.precision?.let { Precision.create(it).getOrNull() },
                    scale = columnCommand.scale?.let { Scale.create(it).getOrNull() }
                )
            ).getOrNull()
        }

        if (errors.isNotEmpty()) {
            return TableError.ValidationFailed(errors).left()
        }

        val createResult: Either<TableError, ADTable> = ADTable.create(
            CreateTableParams(
                metadata = metadata,
                name = name,
                displayName = displayName,
                description = description,
                accessLevel = command.accessLevel,
                columns = columns
            )
        )

        return when (createResult) {
            is Either.Left -> createResult
            is Either.Right -> {
                val savedTable: Either<TableError, ADTable> = operations.save(createResult.value)
                
                when (savedTable) {
                    is Either.Right -> {
                        val event: DomainEvent = TableCreated(
                            metadata = EventMetadata(
                                id = UUID.randomUUID(),
                                timestamp = Instant.now(),
                                user = savedTable.value.metadata.createdBy
                            ),
                            tableId = savedTable.value.metadata.id,
                            tableName = savedTable.value.name.value
                        )
                        eventPublisher.publish(event)
                    }
                    is Either.Left -> Unit
                }
                
                savedTable
            }
        }
    }
}