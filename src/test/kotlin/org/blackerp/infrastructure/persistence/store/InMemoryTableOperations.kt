package org.blackerp.infrastructure.persistence.store

import arrow.core.left
import arrow.core.Either
import arrow.core.right
import org.blackerp.domain.table.ADTable
import org.blackerp.domain.table.TableError
import org.blackerp.domain.table.TableOperations
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class InMemoryTableOperations : TableOperations {
    private val tables = ConcurrentHashMap<UUID, ADTable>()
    private val nameIndex = ConcurrentHashMap<String, UUID>()

    override suspend fun save(table: ADTable): Either<TableError, ADTable> =
        findByName(table.name.value).fold(
            { error -> error.left() },
            { existing ->
                if (existing != null && existing.metadata.id != table.metadata.id) {
                    TableError.DuplicateTable(table.name.value).left()
                } else {
                    tables[table.metadata.id] = table
                    nameIndex[table.name.value] = table.metadata.id
                    table.right()
                }
            }
        )

    override suspend fun findById(id: UUID): Either<TableError, ADTable?> =
        Either.catch {
            tables[id]
        }.mapLeft { 
            TableError.StorageError(it) 
        }

    override suspend fun findByName(name: String): Either<TableError, ADTable?> =
        Either.catch {
            nameIndex[name]?.let { tables[it] }
        }.mapLeft { 
            TableError.StorageError(it) 
        }

    override suspend fun delete(id: UUID): Either<TableError, Unit> =
        Either.catch {
            tables[id]?.let { table ->
                nameIndex.remove(table.name.value)
                tables.remove(id)
            }
            Unit
        }.mapLeft {
            TableError.StorageError(it)
        }
}
