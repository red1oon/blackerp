package org.blackerp.domain.ad.table

import arrow.core.Either
import arrow.core.right
import arrow.core.left
import java.util.UUID
import org.blackerp.domain.core.error.TableError

data class TableRelationship(
    val id: UUID,
    val sourceTable: UUID,
    val targetTable: UUID,
    val type: RelationshipType,
    val sourceColumn: String,
    val targetColumn: String,
    val onDelete: CascadeType,
    val onUpdate: CascadeType
) {
    companion object {
        fun create(params: CreateRelationshipParams): Either<TableError, TableRelationship> {
            if (params.sourceTable == params.targetTable) {
                return TableError.ValidationError(
                    message = "Self-referential relationships not supported",
                    violations = listOf(
                        TableError.Violation("targetTable", "Cannot reference same table", params.targetTable)
                    )
                ).left()
            }
            
            return TableRelationship(
                id = UUID.randomUUID(),
                sourceTable = params.sourceTable,
                targetTable = params.targetTable,
                type = params.type,
                sourceColumn = params.sourceColumn,
                targetColumn = params.targetColumn,
                onDelete = params.onDelete,
                onUpdate = params.onUpdate
            ).right()
        }
    }
}

enum class RelationshipType {
    ONE_TO_ONE,
    ONE_TO_MANY,
    MANY_TO_ONE
}

enum class CascadeType {
    NO_ACTION,
    CASCADE,
    SET_NULL,
    RESTRICT
}

data class CreateRelationshipParams(
    val sourceTable: UUID,
    val targetTable: UUID,
    val type: RelationshipType,
    val sourceColumn: String,
    val targetColumn: String,
    val onDelete: CascadeType = CascadeType.NO_ACTION,
    val onUpdate: CascadeType = CascadeType.NO_ACTION
)
