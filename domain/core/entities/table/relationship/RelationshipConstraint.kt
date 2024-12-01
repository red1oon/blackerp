package org.blackerp.domain.table.relationship

import arrow.core.Either
import org.blackerp.domain.DomainEntity
import org.blackerp.shared.ValidationError

interface RelationshipConstraint : DomainEntity {
    suspend fun validate(relationship: TableRelationship): Either<ValidationError, Unit>
}
