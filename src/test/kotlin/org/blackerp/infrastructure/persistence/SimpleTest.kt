// File: src/test/kotlin/org/blackerp/infrastructure/persistence/SimpleTest.kt
package org.blackerp.infrastructure.persistence

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import org.blackerp.config.TestConfig
import org.springframework.jdbc.core.JdbcTemplate
import org.slf4j.LoggerFactory
import java.time.Instant
import java.util.UUID

class SimpleTest : DescribeSpec({
    
    val jdbcTemplate = TestConfig().jdbcTemplate(TestConfig().dataSource())
    val logger = LoggerFactory.getLogger(SimpleTest::class.java)

    beforeSpec {
        // Create test table if it doesn't exist
        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS ad_table (
                id VARCHAR(36) PRIMARY KEY,
                name VARCHAR(100) NOT NULL,
                display_name VARCHAR(100) NOT NULL,
                description VARCHAR(255),
                access_level VARCHAR(50),
                created TIMESTAMP NOT NULL,
                created_by VARCHAR(100) NOT NULL,
                updated TIMESTAMP,
                updated_by VARCHAR(100),
                version INTEGER DEFAULT 0,
                active BOOLEAN DEFAULT true
            )
        """)
    }

    afterSpec {
        jdbcTemplate.execute("DROP TABLE IF EXISTS ad_table")
    }

    describe("Database Operations") {
        it("should verify database connection") {
            val result = jdbcTemplate.queryForObject("SELECT 1", Int::class.java)
            result shouldBe 1
        }

        it("should insert into ad_table") {
            // given
            val id = UUID.randomUUID()
            val now = Instant.now()

            // when
            val insertCount = jdbcTemplate.update("""
                INSERT INTO ad_table (
                    id, name, display_name, created, created_by
                ) VALUES (?, ?, ?, ?, ?)
                """,
                id,
                "test_table",
                "Test Table",
                now,
                "test-user"
            )

            // then
            insertCount shouldBe 1

            val result = jdbcTemplate.queryForMap(
                "SELECT * FROM ad_table WHERE id = ?",
                id
            )

            result["name"] shouldBe "test_table"
            result["display_name"] shouldBe "Test Table"
        }
    }
})
