package org.blackerp.domain.table.constraint

import arrow.core.Either
import arrow.core.right
import arrow.core.left
import org.blackerp.domain.EntityMetadata 
import org.blackerp.domain.values.ColumnName
import org.blackerp.shared.ValidationError
import org.blackerp.domain.table.definition.TableDefinition

data class NotNullConstraint(
    override val metadata: EntityMetadata,
    val column: ColumnName
) : TableConstraint {
    override suspend fun validate(table: TableDefinition): Either<ValidationError, Unit> {
        val columnExists = table.columns.any { it.name == column }
        
        return if (columnExists) {
            Unit.right()
        } else {
            ValidationError.InvalidValue("Column not found in table: ${column.value}").left()
        }
    }
}