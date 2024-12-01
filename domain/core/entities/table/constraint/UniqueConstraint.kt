
package org.blackerp.domain.table.constraint

import org.blackerp.domain.table.definition.TableDefinition
import arrow.core.Either
import arrow.core.right
import arrow.core.left
import org.blackerp.domain.EntityMetadata
import org.blackerp.domain.values.ColumnName
import org.blackerp.shared.ValidationError

data class UniqueConstraint(
    override val metadata: EntityMetadata,
    val columns: List<ColumnName>
) : TableConstraint {
    override suspend fun validate(table: TableDefinition): Either<ValidationError, Unit> {
        val tableColumns = table.columns.map { it.name }
        val invalidColumns = columns.filter { it !in tableColumns }
        
        return if (invalidColumns.isEmpty()) {
            Unit.right()
        } else {
            ValidationError.InvalidValue(
                "Columns not found in table: ${invalidColumns.joinToString { it.value }}"
            ).left()
        }
    }
}
