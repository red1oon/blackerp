package org.blackerp.domain.tenant

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import org.blackerp.shared.TestFactory
import org.blackerp.domain.table.TenantAwareTable
import java.util.UUID 

class TenantAwareTableTest : DescribeSpec({
    describe("TenantAwareTable") {
        it("should wrap ADTable with tenant ID") {
            // given
            val table = TestFactory.createTestTable()
            val tenantId = UUID.randomUUID()
            
            // when
            val tenantAwareTable = TenantAwareTable.from(table, tenantId)
            
            // then
            tenantAwareTable.tenantId shouldBe tenantId
            tenantAwareTable.metadata shouldBe table.metadata
            tenantAwareTable.name.value shouldBe table.name.value
            tenantAwareTable.displayName.value shouldBe table.displayName.value
        }
    }
})
