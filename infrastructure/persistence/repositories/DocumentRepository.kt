package org.blackerp.infrastructure.persistence.repositories

import org.springframework.stereotype.Repository
import org.blackerp.domain.core.ad.document.*
import arrow.core.Either
import arrow.core.right
import arrow.core.left
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.util.UUID

@Repository
class DocumentRepository : DocumentOperations {
   override suspend fun create(document: Document): Either<DocumentError, Document> = document.right()

   override suspend fun update(id: UUID, document: Document): Either<DocumentError, Document> = document.right()

   override suspend fun findById(id: UUID): Either<DocumentError, Document?> = null.right()

   override suspend fun search(criteria: SearchCriteria): Flow<Document> = flowOf()

   override suspend fun delete(id: UUID): Either<DocumentError, Unit> = Unit.right()

   override suspend fun changeStatus(id: UUID, status: DocumentStatus): Either<DocumentError, Document> = 
       DocumentError.NotFound(id).left()
       
   override suspend fun getHistory(id: UUID): Flow<DocumentChange> = flowOf()
}
