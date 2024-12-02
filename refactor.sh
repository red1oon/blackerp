#!/bin/bash

# Create event classes under domain/events
cat > domain/events/RelationshipEvents.kt << 'EOL'
package org.blackerp.domain.events

import java.util.UUID
import org.blackerp.domain.ad.table.RelationshipType
import org.blackerp.domain.ad.table.CascadeType

sealed class RelationshipEvent : DomainEvent {
    data class RelationshipCreated(
        override val metadata: EventMetadata,
        val relationshipId: UUID,
        val sourceTableId: UUID,
        val targetTableId: UUID,
        val type: RelationshipType,
        val sourceColumn: String,
        val targetColumn: String,
        val onDelete: CascadeType,
        val onUpdate: CascadeType
    ) : RelationshipEvent()

    data class RelationshipModified(
        override val metadata: EventMetadata,
        val relationshipId: UUID,
        val previousType: RelationshipType,
        val newType: RelationshipType,
        val previousOnDelete: CascadeType,
        val newOnDelete: CascadeType,
        val previousOnUpdate: CascadeType,
        val newOnUpdate: CascadeType
    ) : RelationshipEvent()

    data class RelationshipDeleted(
        override val metadata: EventMetadata,
        val relationshipId: UUID,
        val sourceTableId: UUID,
        val targetTableId: UUID
    ) : RelationshipEvent()
}
EOL

cat > domain/events/ConstraintEvents.kt << 'EOL'
package org.blackerp.domain.events

import java.util.UUID
import org.blackerp.domain.ad.table.ConstraintType

sealed class ConstraintEvent : DomainEvent {
    data class ConstraintCreated(
        override val metadata: EventMetadata,
        val constraintId: UUID,
        val tableId: UUID,
        val name: String,
        val type: ConstraintType,
        val columns: List<String>,
        val expression: String?
    ) : ConstraintEvent()

    data class ConstraintModified(
        override val metadata: EventMetadata,
        val constraintId: UUID,
        val previousName: String,
        val newName: String,
        val previousColumns: List<String>,
        val newColumns: List<String>,
        val previousExpression: String?,
        val newExpression: String?
    ) : ConstraintEvent()

    data class ConstraintDeleted(
        override val metadata: EventMetadata,
        val constraintId: UUID,
        val tableId: UUID,
        val name: String
    ) : ConstraintEvent()

    data class ConstraintViolated(
        override val metadata: EventMetadata,
        val constraintId: UUID,
        val tableId: UUID,
        val violationType: ViolationType,
        val violationDetails: String
    ) : ConstraintEvent()
}

enum class ViolationType {
    UNIQUE_VIOLATION,
    CHECK_VIOLATION,
    FOREIGN_KEY_VIOLATION
}
EOL

cat > domain/events/handlers/RelationshipEventHandler.kt << 'EOL'
package org.blackerp.domain.events.handlers

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
EOL

cat > domain/events/handlers/ConstraintEventHandler.kt << 'EOL'
package org.blackerp.domain.events.handlers

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
EOL

cat > domain/events/publishers/DomainEventPublisher.kt << 'EOL'
package org.blackerp.domain.events.publishers

import org.springframework.stereotype.Component
import org.springframework.context.ApplicationEventPublisher
import org.blackerp.domain.events.DomainEvent
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import java.util.UUID

@Component
class DomainEventPublisher(
    private val applicationEventPublisher: ApplicationEventPublisher
) {
    private val logger = LoggerFactory.getLogger(DomainEventPublisher::class.java)

    fun publish(event: DomainEvent) {
        val correlationId = MDC.get("correlationId") ?: UUID.randomUUID().toString()
        MDC.put("correlationId", correlationId)
        
        try {
            logger.debug("Publishing event: {} with correlationId: {}", 
                event.javaClass.simpleName, correlationId)
            
            applicationEventPublisher.publishEvent(event)
            
            logger.debug("Successfully published event: {} with correlationId: {}", 
                event.javaClass.simpleName, correlationId)
        } catch (e: Exception) {
            logger.error("Failed to publish event: {} with correlationId: {}", 
                event.javaClass.simpleName, correlationId, e)
            throw e
        } finally {
            MDC.remove("correlationId")
        }
    }
}
EOL

echo "Event system implementation completed with correct paths under domain/events/"