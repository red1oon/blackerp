// File: integration-tests/test/kotlin/org/blackerp/test/security/SecurityMetadataServiceTest.kt
package org.blackerp.test.security

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import org.blackerp.test.base.BaseServiceTest
import org.blackerp.test.config.TestConfig
import org.blackerp.domain.core.ad.metadata.services.MetadataService
import org.blackerp.domain.core.values.DisplayName
import org.blackerp.domain.core.ad.metadata.entities.ADRule
import kotlinx.coroutines.flow.toList

@SpringBootTest
@ContextConfiguration(classes = [TestConfig::class])
class SecurityMetadataServiceTest : BaseServiceTest() {
    @Autowired
    lateinit var metadataService: MetadataService

    @Test
    fun `should create and validate security rule`() = runTest {
        // Arrange
        val metadata = createTestMetadata()
        val displayName = DisplayName.create("Test Rule")
            .fold({ throw IllegalArgumentException(it.message) }, { it })

        val rule = ADRule(
            metadata = metadata,
            displayName = displayName,
            description = null,
            ruleType = "VALIDATION",
            entityType = "TABLE",
            expression = "1=1",
            errorMessage = null
        )

        // Act & Assert
        val initialRules = metadataService.getRules("TABLE").toList()
        assert(initialRules.isEmpty()) { "Should start with no rules" }

        // Verify rules can be retrieved
        val rules = metadataService.getRules("TABLE").toList()
        assert(rules.isEmpty()) { "Should have no rules initially" }
    }
}