// domain-entities/src/main/kotlin/org/blackerp/domain/tenant/TenantAware.kt
package org.blackerp.domain.core.tenant

import java.util.UUID

interface TenantAware {
    val tenantId: UUID
}