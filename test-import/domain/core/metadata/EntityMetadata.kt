package org.blackerp.domain.core.metadata

import java.util.UUID
import java.time.Instant

data class EntityMetadata(
    val id: UUID = UUID.randomUUID(),
    val created: Instant = Instant.now(),
    val createdBy: String
)