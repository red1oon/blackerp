package org.blackerp.domain.core.ad.table

import arrow.core.Either
import org.blackerp.domain.core.error.TableError
import java.util.UUID

interface RelationshipOperations {
    suspend fun save(relationship: TableRelationship): Either<TableError, TableRelationship>
    suspend fun findBySourceTable(tableId: UUID): Either<TableError, List<TableRelationship>>
    suspend fun findByTargetTable(tableId: UUID): Either<TableError, List<TableRelationship>>
    suspend fun delete(id: UUID): Either<TableError, Unit>
}
