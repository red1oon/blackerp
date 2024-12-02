// domain-entities/src/main/kotlin/org/blackerp/domain/event/DomainEvent.kt
package org.blackerp.domain.event

interface DomainEvent {
    val metadata: EventMetadata
}