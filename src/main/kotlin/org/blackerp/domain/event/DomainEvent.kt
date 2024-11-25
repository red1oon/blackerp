package org.blackerp.domain.event

interface DomainEvent {
    val metadata: EventMetadata
}
