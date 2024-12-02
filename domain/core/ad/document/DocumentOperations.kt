package org.blackerp.domain.ad.document

import arrow.core.Either
import java.util.UUID

interface DocumentOperations {
    suspend fun save(document: Document): Either<DocumentError, Document>
    suspend fun findById(id: UUID): Either<DocumentError, Document?>
    suspend fun delete(id: UUID): Either<DocumentError, Unit>
    suspend fun updateStatus(id: UUID, status: DocumentStatus): Either<DocumentError, Document>
}
