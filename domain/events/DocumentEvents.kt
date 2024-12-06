package org.blackerp.domain.events

import org.blackerp.domain.core.ad.document.DocumentStatus
import java.util.UUID

sealed class DocumentEvent : DomainEvent {
    data class StatusChanged(
        override val metadata: EventMetadata,
        val documentId: UUID,
        val previousStatus: DocumentStatus,
        val newStatus: DocumentStatus,
        val reason: String?
    ) : DocumentEvent()

    data class DocumentCreated(
        override val metadata: EventMetadata,
        val documentId: UUID,
        val type: String,
        val status: DocumentStatus
    ) : DocumentEvent()

    data class DocumentModified(
        override val metadata: EventMetadata,
        val documentId: UUID,
        val changes: Map<String, ChangePair>
    ) : DocumentEvent()
}