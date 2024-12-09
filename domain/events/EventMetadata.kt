package org.blackerp.domain.events

import java.time.Instant
import java.util.UUID

data class EventMetadata(
    val id: UUID = UUID.randomUUID(),
    val timestamp: Instant = Instant.now(),
    val user: String,
    val version: Int = 1,
    val correlationId: String? = null
)
