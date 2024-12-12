// File: integration-tests/test/kotlin/org/blackerp/test/domain/validation/TableValidatorTest.kt
package org.blackerp.test.domain

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import org.blackerp.test.base.BaseServiceTest
import org.blackerp.domain.core.ad.table.*
import org.blackerp.domain.core.values.*
import arrow.core.Either

@SpringBootTest
@ActiveProfiles("test")
class TableValidatorTest : BaseServiceTest() {

    @Configuration
    class TestConfig {
        // Configuration can be empty as we're using the default test context
    }

    @Test
    fun `should validate AD-compliant table metadata`() = runTest {
        val result = Either.catch {
            val tableName = TableName.create("test_table").fold(
                { error -> throw IllegalArgumentException(error.message) },
                { it }
            )
            val displayName = DisplayName.create("Test Table").fold(
                { error -> throw IllegalArgumentException(error.message) },
                { it }
            )
            CreateTableCommand(
                name = tableName,
                displayName = displayName,
                accessLevel = AccessLevel.ORGANIZATION,
                columns = emptyList()
            )
        }
        assert(result.isRight()) { "Valid AD metadata should pass validation" }
    }

    @Test
    fun `should enforce AD naming conventions`() = runTest {
        val result = Either.catch {
            TableName.create("Invalid").fold(
                { error -> throw IllegalArgumentException(error.message) },
                { it }
            )
        }
        assert(result.isLeft()) { "Should enforce AD naming conventions" }
    }
}