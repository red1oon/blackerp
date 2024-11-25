package org.blackerp.domain

import org.blackerp.domain.values.EventMetadata

interface DomainEvent {
    val metadata: EventMetadata
}
