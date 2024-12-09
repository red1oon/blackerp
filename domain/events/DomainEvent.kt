package org.blackerp.domain.events

interface DomainEvent {
    val metadata: EventMetadata
}

data class ChangePair(
    val oldValue: Any?,
    val newValue: Any?
) {
    val hasChanged: Boolean get() = oldValue != newValue
}
