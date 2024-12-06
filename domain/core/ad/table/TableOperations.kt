package org.blackerp.domain.core.ad.table

import arrow.core.Either
import kotlinx.coroutines.flow.Flow 
import org.blackerp.domain.core.error.TableError
import java.util.UUID

interface TableOperations {
   suspend fun findAll(): Either<TableError, List<ADTable>>
   suspend fun findById(id: UUID): Either<TableError, ADTable?>
   suspend fun save(table: ADTable): Either<TableError, ADTable>
   suspend fun delete(id: UUID): Either<TableError, Unit>
}

interface TableValidator {
   suspend fun validateCreate(command: org.blackerp.domain.core.ad.table.CreateTableCommand): Either<TableError, org.blackerp.domain.core.ad.table.CreateTableCommand>
   suspend fun validateUpdate(id: UUID, command: org.blackerp.domain.core.ad.table.UpdateTableCommand): Either<TableError, org.blackerp.domain.core.ad.table.UpdateTableCommand>
}