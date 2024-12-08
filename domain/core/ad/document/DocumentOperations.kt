// File: domain/core/ad/document/DocumentOperations.kt
package org.blackerp.domain.core.ad.document

import arrow.core.Either
import java.util.UUID
import kotlinx.coroutines.flow.Flow

interface DocumentOperations {
    suspend fun create(document: Document): Either<DocumentError, Document>
    suspend fun update(id: UUID, document: Document): Either<DocumentError, Document>
    suspend fun findById(id: UUID): Either<DocumentError, Document?>
    suspend fun search(criteria: SearchCriteria): Flow<Document>
    suspend fun delete(id: UUID): Either<DocumentError, Unit>
    suspend fun changeStatus(id: UUID, status: DocumentStatus): Either<DocumentError, Document>
}
