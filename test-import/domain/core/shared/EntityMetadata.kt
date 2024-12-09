package org.blackerp.domain.core.shared

import java.util.UUID
import org.blackerp.domain.core.DomainEntity

data class EntityMetadata(
    override val id: String,
    val audit: AuditInfo,
    val version: VersionInfo = VersionInfo()
) : DomainEntity

data class AuditInfo(
    val createdBy: String,
    val updatedBy: String
)

data class VersionInfo(
    val version: Int = 1,
    val active: Boolean = true
)
