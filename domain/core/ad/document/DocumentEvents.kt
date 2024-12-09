package org.blackerp.domain.core.ad.document

import org.blackerp.domain.events.DomainEvent
import org.blackerp.domain.events.EventMetadata
import java.util.UUID

sealed class DocumentEvent : DomainEvent {
    data class ProcessExecuted(
        override val metadata: EventMetadata,
        val documentId: UUID,
        val processId: UUID,
        val success: Boolean,
        val message: String
    ) : DocumentEvent()

    // ... existing event classes ...
}
