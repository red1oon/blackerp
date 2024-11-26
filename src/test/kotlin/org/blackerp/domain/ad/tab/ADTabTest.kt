
package org.blackerp.domain.ad.tab

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.assertions.arrow.core.shouldBeLeft
import org.blackerp.domain.values.*
import org.blackerp.domain.ad.tab.value.TabName
import org.blackerp.shared.TestFactory

class ADTabTest : DescribeSpec({
    describe("ADTab") {
        it("should create valid tab") {
            // given
            val table = TestFactory.createTestTable()
            val columnName = table.columns.first().name
            
            val params = CreateTabParams(
                metadata = TestFactory.createMetadata(),
                name = TabName.create("test_tab").getOrNull()!!,
                displayName = DisplayName.create("Test Tab").getOrNull()!!,
                description = null,
                table = table,
                queryColumns = listOf(columnName),
                displayColumns = listOf(columnName),
                orderBy = listOf(
                    OrderBySpec(columnName, SortDirection.ASC)
                )
            )

            // when
            val result = ADTab.create(params)

            // then
            result.shouldBeRight()
        }

        it("should validate column existence") {
            // given
            val table = TestFactory.createTestTable()
            val invalidColumnName = ColumnName.create("invalid_column").getOrNull()!!
            
            val params = CreateTabParams(
                metadata = TestFactory.createMetadata(),
                name = TabName.create("test_tab").getOrNull()!!,
                displayName = DisplayName.create("Test Tab").getOrNull()!!,
                description = null,
                table = table,
                queryColumns = listOf(invalidColumnName),
                displayColumns = emptyList(),
                orderBy = emptyList()
            )

            // when
            val result = ADTab.create(params)

            // then
            result.shouldBeLeft()
        }
    }
})
