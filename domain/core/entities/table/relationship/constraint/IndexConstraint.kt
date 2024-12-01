package org.blackerp.domain.table.relationship.constraint

import arrow.core.Either
import arrow.core.right
import org.blackerp.domain.EntityMetadata
import org.blackerp.domain.values.ColumnName
import org.blackerp.domain.table.relationship.RelationshipConstraint
import org.blackerp.domain.table.relationship.TableRelationship
import org.blackerp.shared.ValidationError

data class IndexConstraint(
    override val metadata: EntityMetadata,
    val columns: List<ColumnName>,
    val unique: Boolean = false
) : RelationshipConstraint {
    override suspend fun validate(relationship: TableRelationship): Either<ValidationError, Unit> = Unit.right()
}
