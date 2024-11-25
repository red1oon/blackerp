package org.blackerp.domain.table.definition

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import org.blackerp.domain.DomainEntity
import org.blackerp.domain.EntityMetadata
import org.blackerp.domain.values.*
import org.blackerp.domain.table.ColumnDefinition
import org.blackerp.domain.table.TableError
import org.blackerp.domain.table.constraint.TableConstraint
import org.blackerp.domain.table.behavior.TableBehavior
import org.blackerp.shared.ValidationError
import kotlinx.coroutines.runBlocking

data class TableDefinition(
    override val metadata: EntityMetadata,
    val name: TableName,
    val displayName: DisplayName,
    val description: Description?,
    val accessLevel: AccessLevel,
    val columns: List<ColumnDefinition>,
    val constraints: List<TableConstraint>,
    val behaviors: List<TableBehavior>
) : DomainEntity {

    companion object {
        suspend fun create(params: CreateTableParams): Either<TableError, TableDefinition> {
            val errors = mutableListOf<ValidationError>()
            
            // Basic validation
            if (params.columns.isEmpty()) {
                errors.add(ValidationError.Required("at least one column"))
            }

            // Check for duplicate column names
            val duplicateColumns = params.columns
                .groupBy { it.name.value }
                .filter { it.value.size > 1 }
                .keys
            
            if (duplicateColumns.isNotEmpty()) {
                errors.add(ValidationError.InvalidValue(
                    "Duplicate column names: ${duplicateColumns.joinToString()}"
                ))
            }

            if (errors.isNotEmpty()) {
                return TableError.ValidationFailed(errors).left()
            }

            val table = TableDefinition(
                metadata = params.metadata,
                name = params.name,
                displayName = params.displayName,
                description = params.description,
                accessLevel = params.accessLevel,
                columns = params.columns,
                constraints = params.constraints,
                behaviors = params.behaviors
            )

            // Validate all constraints
            val constraintErrors = table.constraints
                .map { it.validate(table) }
                .filter { it.isLeft() }
                .flatMap { (it as Either.Left).value.let { listOf(it) } }

            if (constraintErrors.isNotEmpty()) {
                return TableError.ValidationFailed(constraintErrors).left()
            }

            return table.right()
        }
    }

    suspend fun validate(): Either<TableError, Unit> {
        val errors = constraints
            .map { it.validate(this) }
            .filter { it.isLeft() }
            .flatMap { (it as Either.Left).value.let { listOf(it) } }

        return if (errors.isEmpty()) {
            Unit.right()
        } else {
            TableError.ValidationFailed(errors).left()
        }
    }
}

data class CreateTableParams(
    val metadata: EntityMetadata,
    val name: TableName,
    val displayName: DisplayName,
    val description: Description?,
    val accessLevel: AccessLevel,
    val columns: List<ColumnDefinition>,
    val constraints: List<TableConstraint> = emptyList(),
    val behaviors: List<TableBehavior> = emptyList()
)
