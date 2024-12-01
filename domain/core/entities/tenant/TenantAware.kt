// domain-entities/src/main/kotlin/org/blackerp/domain/tenant/TenantAware.kt
package org.blackerp.domain.tenant

import java.util.UUID

interface TenantAware {
    val tenantId: UUID
}