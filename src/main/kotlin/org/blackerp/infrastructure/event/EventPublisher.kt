package org.blackerp.infrastructure.event

import arrow.core.Either
import org.blackerp.domain.event.DomainEvent
import org.blackerp.domain.table.TableError

interface EventPublisher {
    suspend fun publish(event: DomainEvent): Either<TableError, Unit>
}
