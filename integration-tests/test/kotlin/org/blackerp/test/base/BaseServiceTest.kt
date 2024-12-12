package org.blackerp.test.base

import java.util.UUID
import kotlinx.coroutines.runBlocking
import org.blackerp.domain.core.shared.EntityMetadata
import org.blackerp.domain.core.shared.AuditInfo
import org.blackerp.domain.core.shared.VersionInfo
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

    protected fun createTestMetadata(): EntityMetadata {
        return EntityMetadata(
            id = UUID.randomUUID().toString(),
            audit = AuditInfo(
                createdBy = "test-user",
                updatedBy = "test-user"
            ),
            version = VersionInfo()
        )
    }

    protected fun <T> runTest(block: suspend () -> T): T = runBlocking { block() }
}
