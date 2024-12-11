package org.blackerp.domain.events

import java.util.UUID

sealed class TableEvent : DomainEvent {
    data class TableCreated(
        override val metadata: EventMetadata,
        val tableId: UUID,
        val name: String,
        val displayName: String
    ) : TableEvent()

    data class TableModified(
        override val metadata: EventMetadata,
        val tableId: UUID,
        val changes: Map<String, ChangePair>
    ) : TableEvent()

    data class TableDeleted(
        override val metadata: EventMetadata,
        val tableId: UUID
    ) : TableEvent()
}
