package org.blackerp.domain.ad.table

import arrow.core.Either
import arrow.core.right
import arrow.core.left
import java.util.UUID
import org.blackerp.domain.core.error.TableError

data class TableConstraint(
    val id: UUID,
    val tableId: UUID,
    val name: String,
    val type: ConstraintType,
    val columns: List<String>,
    val expression: String?
) {
    companion object {
        fun create(params: CreateConstraintParams): Either<TableError, TableConstraint> {
            if (params.columns.isEmpty() && params.expression == null) {
                return TableError.ValidationError(
                    message = "Constraint must specify either columns or expression",
                    violations = listOf(
                        TableError.Violation("constraint", "No columns or expression specified", params.name)
                    )
                ).left()
            }
            
            return TableConstraint(
                id = UUID.randomUUID(),
                tableId = params.tableId,
                name = params.name,
                type = params.type,
                columns = params.columns,
                expression = params.expression
            ).right()
        }
    }
}

enum class ConstraintType {
    UNIQUE,
    CHECK,
    FOREIGN_KEY
}

data class CreateConstraintParams(
    val tableId: UUID,
    val name: String,
    val type: ConstraintType,
    val columns: List<String> = emptyList(),
    val expression: String? = null
)
