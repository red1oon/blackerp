package org.blackerp.domain.core.values

import java.time.Instant
import java.util.UUID

data class EventMetadata(
    val id: UUID,
    val timestamp: Instant,
    val user: String
)
