package org.blackerp.domain.core.security.role

import org.blackerp.domain.core.DomainEntity
import org.blackerp.domain.core.shared.EntityMetadata
import java.util.UUID

data class Role(
    override val id: String,
    val name: String,
    val description: String?,
    val permissions: Set<String>,
    val clientId: UUID,
    val organizationId: UUID?,
    val isActive: Boolean = true,
    val metadata: EntityMetadata
) : DomainEntity
