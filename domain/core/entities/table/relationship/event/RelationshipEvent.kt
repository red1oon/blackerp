package org.blackerp.domain.table.relationship.event

import org.blackerp.domain.event.DomainEvent
import org.blackerp.domain.event.EventMetadata
import java.util.UUID

sealed interface RelationshipEvent : DomainEvent

data class RelationshipCreated(
    override val metadata: EventMetadata,
    val relationshipId: UUID,
    val sourceTable: String,
    val targetTable: String
) : RelationshipEvent

data class RelationshipDeleted(
    override val metadata: EventMetadata,
    val relationshipId: UUID
) : RelationshipEvent
