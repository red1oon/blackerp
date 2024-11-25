// File: src/main/kotlin/org/blackerp/domain/table/TableOperations.kt
package org.blackerp.domain.table

import arrow.core.Either
import java.util.UUID

interface TableOperations {
    /**
     * Saves a table to the store
     * @param table The table to save
     * @return Either an error or the saved table
     */
    suspend fun save(table: ADTable): Either<TableError, ADTable>
    
    /**
     * Finds a table by its ID
     * @param id The UUID of the table
     * @return Either an error or the found table (null if not found)
     */
    suspend fun findById(id: UUID): Either<TableError, ADTable?>
    
    /**
     * Finds a table by its name
     * @param name The name of the table
     * @return Either an error or the found table (null if not found)
     */
    suspend fun findByName(name: String): Either<TableError, ADTable?>
    
    /**
     * Deletes a table by its ID
     * @param id The UUID of the table to delete
     * @return Either an error or Unit on success
     */
    suspend fun delete(id: UUID): Either<TableError, Unit>
}
