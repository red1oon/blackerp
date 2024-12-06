// domain/core/ad/document/DocumentOperations.kt
package org.blackerp.domain.core.ad.document

import arrow.core.Either
import kotlinx.coroutines.flow.Flow
import org.blackerp.domain.core.shared.ChangePair
import java.util.UUID
import java.time.Instant

interface DocumentOperations {
    suspend fun create(document: Document): Either<DocumentError, Document>
    suspend fun update(id: UUID, document: Document): Either<DocumentError, Document>
    suspend fun findById(id: UUID): Either<DocumentError, Document?>
    suspend fun search(criteria: SearchCriteria): Flow<Document>
    suspend fun delete(id: UUID): Either<DocumentError, Unit>
    suspend fun changeStatus(id: UUID, status: DocumentStatus): Either<DocumentError, Document>
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
