package org.blackerp.infrastructure.persistence.store

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.assertions.arrow.core.shouldBeLeft
import kotlinx.coroutines.test.runTest
import org.blackerp.domain.ad.tab.*
import org.blackerp.domain.ad.tab.value.TabName
import org.blackerp.domain.table.TableOperations
import org.blackerp.domain.values.*
import org.blackerp.shared.TestFactory
import org.blackerp.config.UnifiedTestConfig
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.jdbc.core.JdbcTemplate
import org.slf4j.LoggerFactory
import java.util.UUID

@SpringBootTest(classes = [UnifiedTestConfig::class])
@ActiveProfiles("test")
class PostgresTabOperationsTest(
    private val jdbcTemplate: JdbcTemplate,
    private val tableOperations: TableOperations
) : DescribeSpec({
    
    val logger = LoggerFactory.getLogger(PostgresTabOperationsTest::class.java)
    lateinit var tabOperations: PostgresTabOperations

    beforeTest {
        tabOperations = PostgresTabOperations(jdbcTemplate, tableOperations)
        
        // Clean up test data
        listOf(
            "DELETE FROM ad_tab_query_column",
            "DELETE FROM ad_tab_display_column",
            "DELETE FROM ad_tab_order_by",
            "DELETE FROM ad_tab",
            "DELETE FROM ad_table"
        ).forEach { sql ->
            try {
                jdbcTemplate.execute(sql)
            } catch (e: Exception) {
                logger.warn("Failed to execute cleanup SQL: $sql", e)
            }
        }
    }

    describe("PostgresTabOperations") {
        context("save") {
            it("should save tab with all related data") {
                runTest {
                    // Create and save the table first
                    val table = TestFactory.createTestTable()
                    logger.debug("Created test table: ${table.name.value}")
                    
                    val savedTableResult = tableOperations.save(table)
                    savedTableResult.shouldBeRight()
                    logger.debug("Saved test table successfully")

                    // Verify the table exists
                    val tableCount = jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM ad_table WHERE name = ?",
                        Int::class.java,
                        table.name.value
                    )
                    tableCount shouldBe 1
                    logger.debug("Verified table exists in database")

                    // Create the tab
                    val columnName = table.columns.first().name
                    val tab = ADTab(
                        metadata = TestFactory.createMetadata(),
                        name = TabName.create("test_tab").getOrNull()!!,
                        displayName = DisplayName.create("Test Tab").getOrNull()!!,
                        description = Description.create("Test Description").getOrNull(),
                        table = table,
                        queryColumns = listOf(columnName),
                        displayColumns = listOf(columnName),
                        orderBy = listOf(
                            OrderBySpec(columnName, SortDirection.ASC)
                        )
                    )
                    logger.debug("Created test tab object")

                    // Save the tab
                    val saveResult = tabOperations.save(tab)
                    logger.debug("Save result: $saveResult")

                    saveResult.shouldBeRight().also { savedTab ->
                        logger.debug("Verifying saved tab...")
                        savedTab.name.value shouldBe "test_tab"
                        savedTab.queryColumns.size shouldBe 1
                        savedTab.queryColumns.first().value shouldBe columnName.value
                        savedTab.displayColumns.size shouldBe 1
                        savedTab.displayColumns.first().value shouldBe columnName.value
                        savedTab.orderBy.size shouldBe 1
                        savedTab.orderBy.first().column.value shouldBe columnName.value
                    }

                    // Verify in database
                    val tabCount = jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM ad_tab WHERE name = ?",
                        Int::class.java,
                        "test_tab"
                    )
                    tabCount shouldBe 1
                    logger.debug("Verified tab exists in database")
                }
            }
        }
    }
})
