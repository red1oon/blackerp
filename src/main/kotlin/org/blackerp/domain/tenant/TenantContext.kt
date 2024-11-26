package org.blackerp.domain.tenant

import java.util.UUID

object TenantContext {
    private val currentTenant = ThreadLocal<UUID>()
    
    fun getCurrentTenant(): UUID? = currentTenant.get()
    
    fun setCurrentTenant(tenantId: UUID) {
        currentTenant.set(tenantId)
    }
    
    fun clear() {
        currentTenant.remove()
    }
}
