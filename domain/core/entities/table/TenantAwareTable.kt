package org.blackerp.domain.table

import org.blackerp.domain.tenant.TenantAware
import org.blackerp.domain.DomainEntity
import org.blackerp.domain.EntityMetadata
import org.blackerp.domain.values.*
import java.util.UUID

data class TenantAwareTable(
    override val metadata: EntityMetadata,
    override val tenantId: UUID,
    val delegate: ADTable
) : DomainEntity by delegate, TenantAware {
    val name: TableName
        get() = delegate.name

    val displayName: DisplayName
        get() = delegate.displayName
    companion object {
        fun from(table: ADTable, tenantId: UUID): TenantAwareTable =
            TenantAwareTable(
                metadata = table.metadata,
                tenantId = tenantId,
                delegate = table
            )
    }
}
