package org.blackerp.test.security

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import org.blackerp.test.base.BaseServiceTest
import org.blackerp.test.config.TestConfig
import org.blackerp.domain.core.ad.metadata.services.RuleEvaluator
import java.util.UUID

@SpringBootTest
@ContextConfiguration(classes = [TestConfig::class])
class SecurityRuleEvaluationTest : BaseServiceTest() {

    @Autowired
    private lateinit var ruleEvaluator: RuleEvaluator

    @Test
    fun `should evaluate simple security rule`() = runTest {
        // Arrange
        val rule = SecurityTestUtils.createTestRule(
            entityType = "TABLE",
            expression = "true"
        )

        // Act
        val context = mapOf(
            "user" to createTestUser(),
            "roles" to listOf("ADMIN")
        )

        // Assert
        val result = ruleEvaluator.evaluate(rule, context)
        assert(result.isRight()) { "Rule evaluation should succeed" }
        result.fold(
            { error -> throw AssertionError("Rule evaluation failed: ${error.message}") },
            { success -> assert(success) { "Rule should evaluate to true" } }
        )
    }

    @Test
    fun `should evaluate role-based security rule`() = runTest {
        // Arrange
        val rule = SecurityTestUtils.createTestRule(
            entityType = "TABLE",
            expression = "hasRole('ADMIN')"
        )

        // Act
        val context = mapOf(
            "user" to createTestUser(),
            "roles" to listOf("ADMIN")
        )

        // Assert
        val result = ruleEvaluator.evaluate(rule, context)
        assert(result.isRight()) { "Rule evaluation should succeed" }
        result.fold(
            { error -> throw AssertionError("Rule evaluation failed: ${error.message}") },
            { success -> assert(success) { "Rule should evaluate to true for admin role" } }
        )
    }

    private fun createTestUser(): Map<String, Any> = mapOf(
        "id" to UUID.randomUUID(),
        "username" to "test-user",
        "clientId" to UUID.randomUUID()
    )
}
