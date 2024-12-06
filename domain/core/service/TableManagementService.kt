package org.blackerp.domain.core.service

import arrow.core.Either
import kotlinx.coroutines.flow.Flow
import org.blackerp.domain.core.error.TableError
import org.blackerp.domain.core.ad.table.*
import org.blackerp.domain.core.values.*
import java.util.UUID

interface TableManagementService {
   suspend fun createTable(command: org.blackerp.domain.core.ad.table.CreateTableCommand): Either<TableError, ADTable>
   suspend fun updateTable(id: UUID, command: org.blackerp.domain.core.ad.table.UpdateTableCommand): Either<TableError, ADTable>  
   suspend fun deleteTable(id: UUID): Either<TableError, Unit>
   suspend fun getTable(id: UUID): Either<TableError, ADTable?>
   suspend fun findTables(criteria: TableSearchCriteria): Flow<ADTable>
}

data class TableSearchCriteria(
   val namePattern: String? = null,
   val accessLevel: AccessLevel? = null,
   val modifiedSince: java.time.Instant? = null,
   val pageSize: Int = 20,
   val page: Int = 0
) 