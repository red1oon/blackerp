package org.blackerp.domain.table.constraint

import arrow.core.Either
import org.blackerp.domain.DomainEntity
import org.blackerp.domain.table.definition.TableDefinition
import org.blackerp.shared.ValidationError

interface TableConstraint : DomainEntity {
    // Make the interface method suspend as well
    suspend fun validate(table: TableDefinition): Either<ValidationError, Unit>
}