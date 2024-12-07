// File: domain/events/WindowEvents.kt
package org.blackerp.domain.events

import org.blackerp.domain.core.ad.window.WindowType
import java.util.UUID

sealed class WindowEvent : DomainEvent {
    data class WindowCreated(
        override val metadata: EventMetadata,
        val windowId: UUID,
        val name: String,
        val type: WindowType
    ) : WindowEvent()

    data class WindowUpdated(
        override val metadata: EventMetadata,
        val windowId: UUID,
        val changes: Map<String, ChangePair>
    ) : WindowEvent()

    data class WindowDeleted(
        override val metadata: EventMetadata,
        val windowId: UUID
    ) : WindowEvent()

    data class TabAdded(
        override val metadata: EventMetadata,
        val windowId: UUID,
        val tabId: UUID,
        val name: String
    ) : WindowEvent()

    data class TabRemoved(
        override val metadata: EventMetadata,
        val windowId: UUID,
        val tabId: UUID
    ) : WindowEvent()
}