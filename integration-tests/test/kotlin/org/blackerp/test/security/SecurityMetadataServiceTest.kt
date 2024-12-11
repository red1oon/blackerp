package org.blackerp.test.security

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.blackerp.application.services.security.SecurityMetadataService
import org.blackerp.test.base.BaseServiceTest
import org.blackerp.domain.core.security.metadata.*
import org.blackerp.domain.core.values.*
import java.util.UUID
import kotlinx.coroutines.runBlocking

class SecurityMetadataServiceTest : BaseServiceTest() {
    @Autowired
    lateinit var securityMetadataService: SecurityMetadataService

    @Test
    fun `should create and validate security rule`() = runBlocking {
        val metadata = createTestMetadata()
        val rule = SecurityRuleDefinition(
            metadata = metadata,
            displayName = DisplayName.create("Test Rule").getOrNull()!!,
            description = null,
            entityType = "TABLE",
            entityId = UUID.randomUUID(),
            roleId = UUID.randomUUID(),
            ruleType = SecurityRuleType.READ_PERMISSION,
            expression = "1=1"
        )

        val result = securityMetadataService.createSecurityRule(rule)
        assert(result.isRight()) { "Security rule creation should succeed" }
    }
}
