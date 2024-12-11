package org.blackerp.application.services.process

import org.springframework.stereotype.Service
import org.blackerp.domain.core.ad.process.*
import org.blackerp.domain.events.ProcessEvent
import org.blackerp.domain.events.DomainEvent
import org.blackerp.domain.events.EventMetadata
import org.blackerp.infrastructure.events.publishers.DomainEventPublisher
import org.blackerp.application.services.core.base.BaseDomainServiceImpl
import org.blackerp.application.services.core.base.SearchCriteria
import arrow.core.*
import kotlinx.coroutines.flow.*
import java.util.UUID

@Service
class ProcessServiceImpl(
    eventPublisher: DomainEventPublisher,
    private val processOperations: ProcessOperations,
    private val processExecutor: ProcessExecutor
) : BaseDomainServiceImpl<ADProcess, ProcessError>(eventPublisher) {

    override suspend fun validateCreate(entity: ADProcess): Either<ProcessError, ADProcess> =
        // Validation will be driven by AD metadata
        entity.right()

    override suspend fun validateUpdate(id: UUID, entity: ADProcess): Either<ProcessError, ADProcess> =
        // Validation will be driven by AD metadata
        entity.right()

    override suspend fun executeCreate(entity: ADProcess): Either<ProcessError, ADProcess> =
        processOperations.save(entity)

    override suspend fun executeUpdate(id: UUID, entity: ADProcess): Either<ProcessError, ADProcess> =
        processOperations.save(entity)

    override suspend fun executeFindById(id: UUID): Either<ProcessError, ADProcess?> =
        processOperations.findById(id)

    override suspend fun executeDelete(id: UUID): Either<ProcessError, Unit> =
        processOperations.delete(id)

    override suspend fun search(criteria: SearchCriteria): Flow<ADProcess> =
        processOperations.search("", criteria.pageSize, criteria.page)

    suspend fun execute(id: UUID, parameters: Map<String, Any>, async: Boolean = false): Either<ProcessError, ProcessResult> =
        processOperations.execute(id, parameters, async)

    override fun handleUnexpectedError(e: Exception): ProcessError =
        ProcessError.ExecutionFailed("Unexpected error: ${e.message}")

    override fun createCreatedEvent(entity: ADProcess): DomainEvent =
        ProcessEvent.ProcessCreated(
            metadata = EventMetadata(user = "system"),
            processId = UUID.fromString(entity.id),
            type = entity.type
        )

    override fun createUpdatedEvent(entity: ADProcess): DomainEvent =
        ProcessEvent.ProcessModified(
            metadata = EventMetadata(user = "system"),
            processId = UUID.fromString(entity.id)
        )

    override fun createDeletedEvent(id: UUID): DomainEvent =
        ProcessEvent.ProcessDeleted(
            metadata = EventMetadata(user = "system"),
            processId = id
        )
}
