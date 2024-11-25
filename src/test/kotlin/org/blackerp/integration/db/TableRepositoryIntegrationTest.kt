package org.blackerp.integration.db

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.assertions.arrow.core.shouldBeRight
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.blackerp.integration.IntegrationTestConfig
import org.springframework.context.annotation.Import
import org.blackerp.infrastructure.persistence.store.PostgresTableOperations
import org.blackerp.shared.TestFactory
import org.springframework.jdbc.core.JdbcTemplate

@SpringBootTest
@ActiveProfiles("test")
@Import(IntegrationTestConfig::class)
class TableRepositoryIntegrationTest(
    private val jdbcTemplate: JdbcTemplate
) : DescribeSpec({
    
    lateinit var tableOperations: PostgresTableOperations
    
    beforeTest {
        tableOperations = PostgresTableOperations(jdbcTemplate)
    }
    
    describe("TableRepository") {
        it("should save and retrieve table") {
            // given
            val table = TestFactory.createTestTable()
            
            // when
            val saveResult = tableOperations.save(table)
            val findResult = tableOperations.findById(table.metadata.id)
            
            // then
            saveResult.shouldBeRight()
            findResult.shouldBeRight()
        }
    }
})
