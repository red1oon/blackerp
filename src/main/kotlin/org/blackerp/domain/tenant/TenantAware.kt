package org.blackerp.domain.tenant

import java.util.UUID

interface TenantAware {
    val tenantId: UUID
}
