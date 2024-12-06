package org.blackerp.domain.core.shared

import java.time.Instant
import java.util.UUID
import org.blackerp.domain.core.DomainEntity

data class AuditInfo(
    val createdAt: Instant = Instant.now(),
    val createdBy: String,
    val updatedAt: Instant = Instant.now(),
    val updatedBy: String
)

data class VersionInfo(
    val version: Int = 1,
    val active: Boolean = true
)

data class EntityMetadata(
    override val id: String = UUID.randomUUID().toString(),
    val audit: AuditInfo,
    val version: VersionInfo = VersionInfo()
) : DomainEntity
