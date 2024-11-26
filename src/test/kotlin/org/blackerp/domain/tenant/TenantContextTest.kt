package org.blackerp.domain.tenant

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import java.util.UUID

class TenantContextTest : DescribeSpec({
    afterTest {
        TenantContext.clear()
    }
    
    describe("TenantContext") {
        it("should manage current tenant") {
            // given
            val tenantId = UUID.randomUUID()
            
            // when
            TenantContext.setCurrentTenant(tenantId)
            
            // then
            TenantContext.getCurrentTenant() shouldBe tenantId
        }
        
        it("should clear tenant context") {
            // given
            val tenantId = UUID.randomUUID()
            TenantContext.setCurrentTenant(tenantId)
            
            // when
            TenantContext.clear()
            
            // then
            TenantContext.getCurrentTenant() shouldBe null
        }
    }
})
