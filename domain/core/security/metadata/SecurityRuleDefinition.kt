package org.blackerp.domain.core.security.metadata

import org.blackerp.domain.core.shared.EntityMetadata
import org.blackerp.domain.core.ad.base.ADObject
import org.blackerp.domain.core.values.*
import java.util.UUID

data class SecurityRuleDefinition(
    override val metadata: EntityMetadata,
    override val displayName: DisplayName,
    override val description: Description?,
    val entityType: String, // TABLE, WINDOW, PROCESS etc.
    val entityId: UUID,
    val roleId: UUID?,
    val ruleType: SecurityRuleType,
    val expression: String?, // AD metadata expression
    val isActive: Boolean = true
) : ADObject {
    override val id: String get() = metadata.id
}

enum class SecurityRuleType {
    READ_PERMISSION,
    WRITE_PERMISSION,
    DELETE_PERMISSION,
    EXECUTE_PERMISSION,
    CUSTOM_PERMISSION
}
