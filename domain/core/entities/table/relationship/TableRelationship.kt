package org.blackerp.domain.table.relationship

import arrow.core.Either
import arrow.core.right
import arrow.core.left
import org.blackerp.domain.DomainEntity
import org.blackerp.domain.EntityMetadata
import org.blackerp.domain.table.TableError
import org.blackerp.domain.values.TableName
import org.blackerp.domain.values.ColumnName
import org.blackerp.domain.table.relationship.value.RelationType
import org.blackerp.domain.table.relationship.value.RelationshipName
import org.blackerp.domain.table.relationship.value.DeleteRule
import org.blackerp.domain.table.relationship.value.UpdateRule
import org.blackerp.shared.ValidationError

data class TableRelationship(
    override val metadata: EntityMetadata,
    val name: RelationshipName,
    val sourceTable: TableName,
    val targetTable: TableName,
    val type: RelationType,
    val sourceColumn: ColumnName,
    val targetColumn: ColumnName,
    val constraints: List<RelationshipConstraint>,
    val deleteRule: DeleteRule = DeleteRule.RESTRICT,    // Added this
    val updateRule: UpdateRule = UpdateRule.RESTRICT     // Added this
) : DomainEntity {
    companion object {
        fun create(params: CreateRelationshipParams): Either<TableError, TableRelationship> {
            // Basic validation first
            val errors = mutableListOf<ValidationError>()
            
            // Validate relationship consistency
            when (params.type) {
                RelationType.ONE_TO_ONE, RelationType.ONE_TO_MANY -> {
                    if (params.sourceTable == params.targetTable && params.sourceColumn == params.targetColumn) {
                        errors.add(ValidationError.InvalidValue("Self-referential relationships must use different column names"))
                    }
                }
                RelationType.MANY_TO_MANY -> {
                    // Many-to-many relationships might need additional validation
                    // Consider junction table requirements
                }
            }

            if (errors.isNotEmpty()) {
                return TableError.ValidationFailed(errors).left()
            }

            return TableRelationship(
                metadata = params.metadata,
                name = params.name,
                sourceTable = params.sourceTable,
                targetTable = params.targetTable,
                type = params.type,
                sourceColumn = params.sourceColumn,
                targetColumn = params.targetColumn,
                constraints = params.constraints
            ).right()
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
