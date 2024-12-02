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
