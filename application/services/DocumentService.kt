package org.blackerp.application.services

import org.springframework.stereotype.Service
import org.blackerp.domain.core.ad.document.*
import arrow.core.Either
import arrow.core.right
import arrow.core.left
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.util.UUID

@Service
class DocumentService : DocumentOperations {
    override suspend fun create(document: Document): Either<DocumentError, Document> =
        document.right()

    override suspend fun findById(id: UUID): Either<DocumentError, Document?> =
        null.right()

    override suspend fun search(criteria: SearchCriteria): Flow<Document> =
        flowOf()

    override suspend fun delete(id: UUID): Either<DocumentError, Unit> =
        Unit.right()

    override suspend fun update(id: UUID, document: Document): Either<DocumentError, Document> =
        document.right()

    override suspend fun changeStatus(id: UUID, status: DocumentStatus): Either<DocumentError, Document> =
        DocumentError.NotFound(id).left()
}
