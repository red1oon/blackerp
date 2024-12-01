package org.blackerp.domain.table.constraint

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import org.blackerp.domain.EntityMetadata
import org.blackerp.domain.values.*
import org.blackerp.domain.table.TableOperations
import org.blackerp.domain.table.ADTable
import org.blackerp.domain.table.ColumnDefinition
import org.blackerp.domain.table.definition.TableDefinition
import org.blackerp.shared.ValidationError
import org.blackerp.shared.ReferenceValidation

data class ReferenceConstraint(
    override val metadata: EntityMetadata,
    val column: ColumnName,
    val referenceTable: TableName,
    val referenceColumn: ColumnName,
    private val tableOperations: TableOperations
) : TableConstraint {
    override suspend fun validate(table: TableDefinition): Either<ValidationError, Unit> {
        // 1. Verify source column exists
        val sourceColumn = table.columns.find { it.name == column }
            ?: return ReferenceValidation.ColumnNotFound(column.value).left()

        // 2. Find reference table
        when (val refTableResult = tableOperations.findByName(referenceTable.value)) {
            is Either.Left -> return ReferenceValidation.ReferenceTableNotFound(referenceTable.value).left()
            is Either.Right -> {
                val refTable = refTableResult.value 
                    ?: return ReferenceValidation.ReferenceTableNotFound(referenceTable.value).left()

                // Convert ADTable to TableDefinition
                val refTableDef = toTableDefinition(refTable)

                // 3. Verify reference column exists
                val refColumn = refTableDef.columns.find { it.name == referenceColumn }
                    ?: return ReferenceValidation.ReferenceColumnNotFound(
                        referenceTable.value,
                        referenceColumn.value
                    ).left()

                // 4. Check type compatibility
                if (sourceColumn.dataType != refColumn.dataType) {
                    return ReferenceValidation.IncompatibleTypes(
                        sourceColumn.name.value,
                        sourceColumn.dataType.name,
                        refColumn.name.value,
                        refColumn.dataType.name
                    ).left()
                }

                // 5. Check length/precision compatibility for certain types
                when (sourceColumn.dataType) {
                    DataType.STRING -> {
                        val sourceLength = sourceColumn.length?.value ?: 0
                        val refLength = refColumn.length?.value ?: 0
                        if (sourceLength > refLength) {
                            return ValidationError.InvalidValue(
                                "Source column length ($sourceLength) cannot be greater than reference column length ($refLength)"
                            ).left()
                        }
                    }
                    DataType.DECIMAL -> {
                        val sourcePrecision = sourceColumn.precision?.value ?: 0
                        val refPrecision = refColumn.precision?.value ?: 0
                        if (sourcePrecision > refPrecision) {
                            return ValidationError.InvalidValue(
                                "Source column precision ($sourcePrecision) cannot be greater than reference column precision ($refPrecision)"
                            ).left()
                        }
                        val sourceScale = sourceColumn.scale?.value ?: 0
                        val refScale = refColumn.scale?.value ?: 0
                        if (sourceScale > refScale) {
                            return ValidationError.InvalidValue(
                                "Source column scale ($sourceScale) cannot be greater than reference column scale ($refScale)"
                            ).left()
                        }
                    }
                    else -> Unit
                }

                return Unit.right()
            }
        }
    }

    private fun toTableDefinition(table: ADTable): TableDefinition {
        return TableDefinition(
            metadata = table.metadata,
            name = table.name,
            displayName = table.displayName,
            description = table.description,
            accessLevel = table.accessLevel,
            columns = table.columns,
            constraints = emptyList(),
            behaviors = emptyList()
        )
    }
}