package org.blackerp.domain.core

import java.util.UUID
import java.time.Instant

data class EntityMetadata(
    val id: UUID = UUID.randomUUID(),
    val created: Instant = Instant.now(),
    val createdBy: String,
    val updated: Instant = Instant.now(),
    val updatedBy: String,
    val version: Int = 0
)
