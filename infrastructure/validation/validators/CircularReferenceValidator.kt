package org.blackerp.infrastructure.validation.validators

import org.springframework.stereotype.Component
import org.blackerp.domain.core.ad.table.TableRelationship
import org.blackerp.domain.core.error.TableError
import arrow.core.Either
import arrow.core.left
import arrow.core.right
import java.util.UUID

@Component
class CircularReferenceValidator {
    fun validate(
        relationship: TableRelationship,
        existingRelationships: List<TableRelationship>
    ): Either<TableError, TableRelationship> {
        val visited = mutableSetOf<UUID>()
        val path = mutableListOf<UUID>()

        fun hasCircularReference(currentTable: UUID): Boolean {
            if (path.contains(currentTable)) {
                return true
            }
            if (!visited.add(currentTable)) {
                return false
            }

            path.add(currentTable)
            val relatedTables = existingRelationships
                .filter { it.sourceTable == currentTable }
                .map { it.targetTable }

            relatedTables.forEach { targetTable ->
                if (hasCircularReference(targetTable)) {
                    return true
                }
            }
            path.removeAt(path.size - 1)
            return false
        }

        return if (hasCircularReference(relationship.sourceTable)) {
            TableError.ValidationError(
                message = "Circular reference detected",
                violations = listOf(
                    TableError.Violation(
                        field = "relationship",
                        message = "Creating this relationship would create a circular reference",
                        value = "${relationship.sourceTable} -> ${relationship.targetTable}"
                    )
                )
            ).left()
        } else {
            relationship.right()
        }
    }
}
