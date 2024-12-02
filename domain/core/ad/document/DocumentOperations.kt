package org.blackerp.domain.ad.document

import arrow.core.Either
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface DocumentOperations {
    suspend fun create(document: Document): Either<DocumentError, Document>
    suspend fun update(id: UUID, document: Document): Either<DocumentError, Document>
    suspend fun findById(id: UUID): Either<DocumentError, Document?>
    suspend fun search(criteria: SearchCriteria): Flow<Document>
    suspend fun delete(id: UUID): Either<DocumentError, Unit>
    suspend fun changeStatus(id: UUID, status: DocumentStatus): Either<DocumentError, Document>
    suspend fun addLine(id: UUID, line: DocumentLine): Either<DocumentError, Document>
    suspend fun updateLine(id: UUID, lineId: UUID, line: DocumentLine): Either<DocumentError, Document>
    suspend fun deleteLine(id: UUID, lineId: UUID): Either<DocumentError, Document>
    suspend fun validateDocument(document: Document): Either<DocumentError, ValidationResult>
    suspend fun getHistory(id: UUID): Flow<DocumentHistory>
}

data class SearchCriteria(
    val types: List<UUID>? = null,
    val statuses: List<DocumentStatus>? = null,
    val dateRange: DateRange? = null,
    val attributes: Map<String, Any>? = null,
    val pageSize: Int = 20,
    val page: Int = 0
)

data class DateRange(
    val from: Instant,
    val to: Instant
)

data class ValidationResult(
    val isValid: Boolean,
    val errors: Map<String, List<String>> = emptyMap()
)

data class DocumentHistory(
    val timestamp: Instant,
    val user: String,
    val action: String,
    val changes: Map<String, ChangePair>
)
