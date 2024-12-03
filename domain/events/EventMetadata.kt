// domain-entities/src/main/kotlin/org/blackerp/domain/event/EventMetadata.kt
package org.blackerp.domain.events

import java.time.Instant
import java.util.UUID

data class EventMetadata(
    val id: UUID,
    val timestamp: Instant,
    val user: String
)