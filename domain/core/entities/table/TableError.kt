package org.blackerp.domain.table

import org.blackerp.domain.DomainException
import org.blackerp.shared.ValidationError

sealed class TableError(message: String) : DomainException(message) {
    data class ValidationFailed(val errors: List<ValidationError>) : 
        TableError("Validation failed: ${errors.joinToString { it.message }}")
    
    data class StorageError(override val cause: Throwable) : 
        TableError("Storage error: ${cause.message}")
    
    data class DuplicateTable(val name: String) : 
        TableError("Table already exists: $name")
    
    data class NotFound(val id: String) : 
        TableError("Table not found: $id")
    
    data class ConcurrentModification(val id: String) : 
        TableError("Concurrent modification detected for table: $id")
}
