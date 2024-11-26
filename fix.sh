#!/bin/bash

# add_tenant_awareness.sh
# Script to add tenant awareness to existing domain model

set -e  # Exit on error

# Base directory structure (already exists)
DOMAIN_DIR="src/main/kotlin/org/blackerp/domain"
TEST_DIR="src/test/kotlin/org/blackerp/domain"

# Create tenant-aware interfaces
cat > "$DOMAIN_DIR/tenant/TenantAware.kt" << 'EOF'
package org.blackerp.domain.tenant

import java.util.UUID

interface TenantAware {
    val tenantId: UUID
}
EOF

# Update ADTable to be tenant-aware
cat > "$DOMAIN_DIR/table/TenantAwareTable.kt" << 'EOF'
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
    companion object {
        fun from(table: ADTable, tenantId: UUID): TenantAwareTable =
            TenantAwareTable(
                metadata = table.metadata,
                tenantId = tenantId,
                delegate = table
            )
    }
}
EOF

# Add tenant filter for operations
cat > "$DOMAIN_DIR/tenant/TenantFilter.kt" << 'EOF'
package org.blackerp.domain.tenant

import arrow.core.Either
import arrow.core.right
import arrow.core.left
import org.blackerp.shared.ValidationError

data class TenantFilter(val tenantId: String?) {
    companion object {
        fun create(tenantId: String?): Either<ValidationError, TenantFilter> =
            when {
                tenantId == null -> TenantFilter(null).right()
                !tenantId.matches(Regex("^[0-9a-fA-F-]{36}$")) ->
                    ValidationError.InvalidFormat("Invalid UUID format").left()
                else -> TenantFilter(tenantId).right()
            }
    }
}
EOF

# Add tenant context for current tenant tracking
cat > "$DOMAIN_DIR/tenant/TenantContext.kt" << 'EOF'
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
EOF

# Add test for tenant awareness
cat > "$TEST_DIR/tenant/TenantAwareTableTest.kt" << 'EOF'
package org.blackerp.domain.tenant

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import org.blackerp.shared.TestFactory
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
            tenantAwareTable.name shouldBe table.name
            tenantAwareTable.displayName shouldBe table.displayName
        }
    }
})
EOF

# Add test for tenant context
cat > "$TEST_DIR/tenant/TenantContextTest.kt" << 'EOF'
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
EOF

echo "Tenant awareness components added successfully!"
echo "Next steps:"
echo "1. Review and run tests"
echo "2. Integrate tenant awareness into existing repositories"
echo "3. Add tenant filtering to queries"