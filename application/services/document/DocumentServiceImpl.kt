package org.blackerp.application.services.document

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import kotlinx.coroutines.flow.Flow
import org.blackerp.application.services.core.base.BaseDomainServiceImpl
import org.blackerp.domain.core.ad.document.*
import org.blackerp.domain.events.DocumentEvent
import org.blackerp.domain.events.DomainEvent
import org.blackerp.domain.events.EventMetadata
import org.blackerp.infrastructure.events.publishers.DomainEventPublisher
import org.springframework.stereotype.Service
import java.util.UUID

/**
 * Document service implementation that enforces AD-centric principles
 * by delegating all business rules and validation to AD metadata.
 */
@Service
class DocumentServiceImpl(
    eventPublisher: DomainEventPublisher,
    private val documentOperations: DocumentOperations
) : BaseDomainServiceImpl<Document, DocumentError>(eventPublisher) {

    override suspend fun validateCreate(entity: Document): Either<DocumentError, Document> =
        // TODO: Implement AD metadata-driven validation
        entity.right()

    override suspend fun validateUpdate(id: UUID, entity: Document): Either<DocumentError, Document> =
        // TODO: Implement AD metadata-driven validation
        entity.right()

    override suspend fun executeCreate(entity: Document): Either<DocumentError, Document> =
        documentOperations.create(entity)

    override suspend fun executeUpdate(id: UUID, entity: Document): Either<DocumentError, Document> =
        documentOperations.update(id, entity)

    override suspend fun executeFindById(id: UUID): Either<DocumentError, Document?> =
        documentOperations.findById(id)

    override suspend fun executeDelete(id: UUID): Either<DocumentError, Unit> =
        documentOperations.delete(id)

    override suspend fun search(criteria: SearchCriteria): Flow<Document> =
        documentOperations.search(DocumentSearchCriteria(
            pageSize = criteria.pageSize,
            page = criteria.page
        ))

    override fun handleUnexpectedError(e: Exception): DocumentError =
        DocumentError.ValidationFailed("Unexpected error: ${e.message}")

    override fun createCreatedEvent(entity: Document): DomainEvent =
        DocumentEvent.DocumentCreated(
            metadata = EventMetadata(user = "system"), // TODO: Get from security context
            documentId = UUID.fromString(entity.id),
            type = entity.type.name,
            status = entity.status
        )

    override fun createUpdatedEvent(entity: Document): DomainEvent =
        DocumentEvent.DocumentModified(
            metadata = EventMetadata(user = "system"), // TODO: Get from security context
            documentId = UUID.fromString(entity.id),
            changes = mapOf() // TODO: Track actual changes
        )

    override fun createDeletedEvent(id: UUID): DomainEvent =
        DocumentEvent.DocumentDeleted(
            metadata = EventMetadata(user = "system"), // TODO: Get from security context
            documentId = id
        )
}
