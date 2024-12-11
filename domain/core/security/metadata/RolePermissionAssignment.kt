package org.blackerp.domain.core.security.metadata

import org.blackerp.domain.core.shared.EntityMetadata
import org.blackerp.domain.core.ad.base.ADObject
import org.blackerp.domain.core.values.*
import java.util.UUID

data class RolePermissionAssignment(
    override val metadata: EntityMetadata,
    override val displayName: DisplayName,
    override val description: Description?,
    val roleId: UUID,
    val permissionType: SecurityRuleType,
    val entityType: String,
    val entityId: UUID,
    val validationRule: String?, // AD metadata validation rule
    val isActive: Boolean = true
) : ADObject {
    override val id: String get() = metadata.id
}
