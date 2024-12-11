// File: integration-tests/test/kotlin/org/blackerp/test/base/BaseServiceTest.kt
package org.blackerp.test.base

import java.util.UUID
import kotlinx.coroutines.runBlocking
import org.blackerp.domain.core.shared.AuditInfo
import org.blackerp.domain.core.shared.EntityMetadata
import org.junit.jupiter.api.extension.ExtendWith
import org.slf4j.LoggerFactory
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest
@ActiveProfiles("test")
abstract class BaseServiceTest {
    protected val logger = LoggerFactory.getLogger(javaClass)

    // Basic test helper for metadata
    protected fun createTestMetadata(): EntityMetadata {
        return EntityMetadata(
                id = UUID.randomUUID().toString(),
                audit = AuditInfo(createdBy = "test-user", updatedBy = "test-user")
        )
    }

    // Simple coroutine test helper
    protected fun <T> runTest(block: suspend () -> T): T = runBlocking { block() }
}
