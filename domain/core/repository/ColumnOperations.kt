package org.blackerp.domain.core.repository

import arrow.core.Either
import org.blackerp.domain.core.ad.table.ColumnDefinition
import org.blackerp.domain.core.error.TableError
import java.util.UUID

interface ColumnOperations {
    suspend fun save(tableId: UUID, column: ColumnDefinition): Either<TableError, ColumnDefinition>
    suspend fun findByTable(tableId: UUID): Either<TableError, List<ColumnDefinition>>
    suspend fun delete(tableId: UUID, columnName: String): Either<TableError, Unit>
}
