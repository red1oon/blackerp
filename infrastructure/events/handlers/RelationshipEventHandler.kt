package org.blackerp.infrastructure.events.handlers

import org.springframework.stereotype.Component
import org.springframework.context.event.EventListener
import org.blackerp.domain.events.RelationshipEvent
import org.slf4j.LoggerFactory

@Component
class RelationshipEventHandler {
    private val logger = LoggerFactory.getLogger(RelationshipEventHandler::class.java)

    @EventListener
    fun handleRelationshipCreated(event: RelationshipEvent.RelationshipCreated) {
        logger.info(
            "Relationship created: {} -> {} (type: {})",
            event.sourceTableId,
            event.targetTableId,
            event.type
        )
    }

    @EventListener
    fun handleRelationshipModified(event: RelationshipEvent.RelationshipModified) {
        logger.info(
            "Relationship modified: {} (type changed: {} -> {})",
            event.relationshipId,
            event.previousType,
            event.newType
        )
    }

    @EventListener
    fun handleRelationshipDeleted(event: RelationshipEvent.RelationshipDeleted) {
        logger.info(
            "Relationship deleted: {} (source: {}, target: {})",
            event.relationshipId,
            event.sourceTableId,
            event.targetTableId
        )
    }
}
