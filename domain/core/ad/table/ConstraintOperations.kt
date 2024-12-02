package org.blackerp.domain.ad.table

import arrow.core.Either
import org.blackerp.domain.core.error.TableError
import java.util.UUID

interface ConstraintOperations {
    suspend fun save(constraint: TableConstraint): Either<TableError, TableConstraint>
    suspend fun findByTable(tableId: UUID): Either<TableError, List<TableConstraint>>
    suspend fun delete(id: UUID): Either<TableError, Unit>
    suspend fun validateConstraint(constraint: TableConstraint): Either<TableError, Unit>
}
