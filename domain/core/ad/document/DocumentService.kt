package org.blackerp.domain.core.ad.document

import arrow.core.Either
import kotlinx.coroutines.flow.Flow
import org.blackerp.domain.core.error.DomainError
import org.blackerp.domain.core.values.DisplayName
import org.blackerp.domain.core.values.Description
import org.blackerp.domain.core.shared.ChangePair
import java.util.UUID
import java.time.Instant

interface DocumentService {
    suspend fun createDocument(command: CreateDocumentCommand): Either<DocumentError, Document>
    suspend fun updateDocument(id: UUID, command: UpdateDocumentCommand): Either<DocumentError, Document>
    suspend fun changeStatus(id: UUID, command: ChangeStatusCommand): Either<DocumentError, Document>
    suspend fun findDocuments(criteria: DocumentSearchCriteria): Flow<Document>
    suspend fun getDocumentHistory(id: UUID): Flow<DocumentChange>
}

data class DocumentChange(
    val id: UUID,
    val documentId: UUID,
    val changedAt: Instant,
    val changedBy: String,
    val changes: Map<String, ChangePair<*>>
)
