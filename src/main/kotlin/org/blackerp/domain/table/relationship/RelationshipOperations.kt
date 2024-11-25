package org.blackerp.domain.table.relationship

import arrow.core.Either
import org.blackerp.domain.table.TableError
import org.blackerp.domain.values.TableName
import java.util.UUID

interface RelationshipOperations {
    suspend fun save(relationship: TableRelationship): Either<TableError, TableRelationship>
    suspend fun findById(id: UUID): Either<TableError, TableRelationship?>
    suspend fun findByTable(tableName: TableName): Either<TableError, List<TableRelationship>>
    suspend fun delete(id: UUID): Either<TableError, Unit>
}
