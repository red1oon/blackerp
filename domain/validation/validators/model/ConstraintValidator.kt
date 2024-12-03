package org.blackerp.domain.validation.validators.model

import org.springframework.stereotype.Component
import org.blackerp.domain.core.ad.table.TableConstraint
import org.blackerp.domain.core.ad.table.ConstraintType
import org.blackerp.domain.core.error.TableError
import arrow.core.Either
import arrow.core.left
import arrow.core.right

@Component
class ConstraintValidator {
    fun validate(constraint: TableConstraint): Either<TableError, TableConstraint> {
        val violations = mutableListOf<TableError.Violation>()

        when (constraint.type) {
            ConstraintType.UNIQUE -> validateUniqueConstraint(constraint, violations)
            ConstraintType.CHECK -> validateCheckConstraint(constraint, violations)
            ConstraintType.FOREIGN_KEY -> validateForeignKeyConstraint(constraint, violations)
        }

        // Common validations
        if (!isValidConstraintName(constraint.name)) {
            violations.add(
                TableError.Violation(
                    field = "name",
                    message = "Constraint name must start with lowercase letter and contain only letters, numbers, and underscores",
                    value = constraint.name
                )
            )
        }

        return if (violations.isEmpty()) {
            constraint.right()
        } else {
            TableError.ValidationError(
                message = "Constraint validation failed",
                violations = violations
            ).left()
        }
    }

    private fun validateUniqueConstraint(
        constraint: TableConstraint,
        violations: MutableList<TableError.Violation>
    ) {
        if (constraint.columns.isEmpty()) {
            violations.add(
                TableError.Violation(
                    field = "columns",
                    message = "Unique constraint must specify at least one column",
                    value = constraint.name
                )
            )
        }
        if (constraint.expression != null) {
            violations.add(
                TableError.Violation(
                    field = "expression",
                    message = "Unique constraint cannot have an expression",
                    value = constraint.name
                )
            )
        }
    }

    private fun validateCheckConstraint(
        constraint: TableConstraint,
        violations: MutableList<TableError.Violation>
    ) {
        if (constraint.expression == null) {
            violations.add(
                TableError.Violation(
                    field = "expression",
                    message = "Check constraint must have an expression",
                    value = constraint.name
                )
            )
        }
        if (constraint.columns.isNotEmpty()) {
            violations.add(
                TableError.Violation(
                    field = "columns",
                    message = "Check constraint cannot specify columns directly",
                    value = constraint.name
                )
            )
        }
    }

    private fun validateForeignKeyConstraint(
        constraint: TableConstraint,
        violations: MutableList<TableError.Violation>
    ) {
        if (constraint.columns.isEmpty()) {
            violations.add(
                TableError.Violation(
                    field = "columns",
                    message = "Foreign key constraint must specify at least one column",
                    value = constraint.name
                )
            )
        }
        if (constraint.expression != null) {
            violations.add(
                TableError.Violation(
                    field = "expression",
                    message = "Foreign key constraint cannot have an expression",
                    value = constraint.name
                )
            )
        }
    }

    private fun isValidConstraintName(name: String): Boolean =
        name.matches(Regex("^[a-z][a-z0-9_]*$"))
}
