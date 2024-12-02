package org.blackerp.domain.core.metadata

import org.blackerp.shared.TimeBasedId
import java.time.Instant
import java.util.UUID

data class EntityMetadata(
    val id: UUID = TimeBasedId.generate(),
    val created: Instant = Instant.now(),
    val createdBy: String,
    val updated: Instant = Instant.now(),
    val updatedBy: String,
    val version: Int = 0,
    val active: Boolean = true
)
