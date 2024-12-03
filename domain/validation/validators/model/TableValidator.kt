package org.blackerp.domain.validation.validators.model

import org.springframework.stereotype.Component
import org.blackerp.domain.core.ad.table.ADTable
import org.blackerp.domain.core.error.TableError
import org.blackerp.domain.core.error.TableError.Violation
import org.blackerp.domain.core.values.DataType
import arrow.core.Either
import arrow.core.left
import arrow.core.right

@Component
class TableValidator {
    fun validateCreate(table: ADTable): Either<TableError, ADTable> {
        val violations = mutableListOf<Violation>()

        // Name validation
        if (!table.name.value.matches(Regex("^[a-z][a-z0-9_]*$"))) {
            violations.add(
                Violation(
                    field = "name",
                    message = "Table name must start with lowercase letter and contain only lowercase letters, numbers, and underscores",
                    value = table.name.value
                )
            )
        }

        // Column validation
        table.columns.forEachIndexed { index, column ->
            validateColumn(column, index, violations)
        }

        // Duplicate column check
        val duplicateColumns = table.columns
            .groupBy { it.name }
            .filter { it.value.size > 1 }
            .keys
        
        duplicateColumns.forEach { name ->
            violations.add(
                Violation(
                    field = "columns",
                    message = "Duplicate column name found",
                    value = name
                )
            )
        }

        // Primary key validation
        if (!table.columns.any { it.isPrimaryKey }) {
            violations.add(
                Violation(
                    field = "columns",
                    message = "Table must have at least one primary key column"
                )
            )
        }

        return if (violations.isEmpty()) {
            table.right()
        } else {
            TableError.ValidationError(
                message = "Table validation failed",
                violations = violations
            ).left()
        }
    }

    private fun validateColumn(
        column: ColumnDefinition,
        index: Int,
        violations: MutableList<Violation>
    ) {
        val prefix = "columns[$index]"

        when (column.dataType) {
            DataType.STRING -> {
                if (column.length == null) {
                    violations.add(
                        Violation(
                            field = "$prefix.length",
                            message = "String column must specify length",
                            value = column.name
                        )
                    )
                }
                if (column.precision != null || column.scale != null) {
                    violations.add(
                        Violation(
                            field = "$prefix",
                            message = "String column cannot have precision or scale",
                            value = column.name
                        )
                    )
                }
            }
            DataType.DECIMAL -> {
                if (column.precision == null) {
                    violations.add(
                        Violation(
                            field = "$prefix.precision",
                            message = "Decimal column must specify precision",
                            value = column.name
                        )
                    )
                }
                if (column.scale == null) {
                    violations.add(
                        Violation(
                            field = "$prefix.scale",
                            message = "Decimal column must specify scale",
                            value = column.name
                        )
                    )
                }
                if (column.length != null) {
                    violations.add(
                        Violation(
                            field = "$prefix",
                            message = "Decimal column cannot have length",
                            value = column.name
                        )
                    )
                }
            }
            else -> {
                if (column.length != null || column.precision != null || column.scale != null) {
                    violations.add(
                        Violation(
                            field = "$prefix",
                            message = "Column of type ${column.dataType} cannot have length, precision, or scale",
                            value = column.name
                        )
                    )
                }
            }
        }

        // Reference validation for foreign keys
        column.reference?.let { ref ->
            if (column.dataType != DataType.UUID) {
                violations.add(
                    Violation(
                        field = "$prefix.reference",
                        message = "Reference columns must be of type UUID",
                        value = column.name
                    )
                )
            }
        }
    }
}
