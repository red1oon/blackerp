package org.blackerp.infrastructure.events.handlers

import org.springframework.stereotype.Component
import org.springframework.context.event.EventListener
import org.blackerp.domain.events.ConstraintEvent
import org.slf4j.LoggerFactory

@Component
class ConstraintEventHandler {
    private val logger = LoggerFactory.getLogger(ConstraintEventHandler::class.java)

    @EventListener
    fun handleConstraintCreated(event: ConstraintEvent.ConstraintCreated) {
        logger.info(
            "Constraint created: {} on table {} (type: {})",
            event.name,
            event.tableId,
            event.type
        )
    }

    @EventListener
    fun handleConstraintModified(event: ConstraintEvent.ConstraintModified) {
        logger.info(
            "Constraint modified: {} (name changed: {} -> {})",
            event.constraintId,
            event.previousName,
            event.newName
        )
    }

    @EventListener
    fun handleConstraintDeleted(event: ConstraintEvent.ConstraintDeleted) {
        logger.info(
            "Constraint deleted: {} from table {}",
            event.name,
            event.tableId
        )
    }

    @EventListener
    fun handleConstraintViolated(event: ConstraintEvent.ConstraintViolated) {
        logger.warn(
            "Constraint violation: {} on table {} (type: {}): {}",
            event.constraintId,
            event.tableId,
            event.violationType,
            event.violationDetails
        )
    }
}
