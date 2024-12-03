package org.blackerp.domain.validation.validators.model

import org.springframework.stereotype.Component
import org.blackerp.domain.core.ad.table.TableRelationship
import org.blackerp.domain.core.error.TableError
import arrow.core.Either
import arrow.core.left
import arrow.core.right

@Component
class RelationshipValidator {
    fun validate(relationship: TableRelationship): Either<TableError, TableRelationship> {
        val violations = mutableListOf<TableError.Violation>()

        // Validate source and target are different
        if (relationship.sourceTable == relationship.targetTable) {
            violations.add(
                TableError.Violation(
                    field = "targetTable",
                    message = "Self-referential relationships not supported",
                    value = relationship.targetTable
                )
            )
        }

        // Validate column names
        if (!isValidColumnName(relationship.sourceColumn)) {
            violations.add(
                TableError.Violation(
                    field = "sourceColumn",
                    message = "Invalid column name format",
                    value = relationship.sourceColumn
                )
            )
        }

        if (!isValidColumnName(relationship.targetColumn)) {
            violations.add(
                TableError.Violation(
                    field = "targetColumn",
                    message = "Invalid column name format",
                    value = relationship.targetColumn
                )
            )
        }

        return if (violations.isEmpty()) {
            relationship.right()
        } else {
            TableError.ValidationError(
                message = "Relationship validation failed",
                violations = violations
            ).left()
        }
    }

    private fun isValidColumnName(name: String): Boolean =
        name.matches(Regex("^[a-z][a-z0-9_]*$"))
}
