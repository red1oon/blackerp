package org.blackerp.domain.events

import org.blackerp.domain.core.ad.process.ProcessType
import java.util.UUID

sealed class ProcessEvent : DomainEvent {
    data class ProcessCreated(
        override val metadata: EventMetadata,
        val processId: UUID,
        val type: ProcessType
    ) : ProcessEvent()

    data class ProcessModified(
        override val metadata: EventMetadata,
        val processId: UUID
    ) : ProcessEvent()

    data class ProcessDeleted(
        override val metadata: EventMetadata,
        val processId: UUID
    ) : ProcessEvent()
}
