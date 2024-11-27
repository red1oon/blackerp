package org.blackerp.infrastructure.event

import arrow.core.Either
import arrow.core.right
import org.blackerp.domain.event.DomainEvent
import org.blackerp.domain.table.TableError
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class DefaultEventPublisher : EventPublisher {
    private val logger = LoggerFactory.getLogger(DefaultEventPublisher::class.java)

    override suspend fun publish(event: DomainEvent): Either<TableError, Unit> {
        logger.info("Publishing event: ${event::class.simpleName} with ID: ${event.metadata.id}")
        // In a real implementation, you might:
        // 1. Persist the event
        // 2. Send to message broker
        // 3. Notify subscribers
        return Unit.right()
    }
}
