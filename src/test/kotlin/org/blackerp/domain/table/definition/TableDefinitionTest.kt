package org.blackerp.domain.table.definition

import arrow.core.right
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import io.mockk.every
import org.blackerp.domain.table.ColumnDefinition
import org.blackerp.domain.values.*
import org.blackerp.shared.TestFactory
import java.time.Instant  
import java.util.UUID     

class TableDefinitionTest : DescribeSpec({
    describe("TableDefinition") { 

        describe("create") {
            it("should create valid table definition") {
                // given
                val columnName = ColumnName.create("test_column").getOrNull()!!
                val mockColumn = mockk<ColumnDefinition> {
                    // Configure the mock to return values when accessed
                    every { name } returns columnName
                    every { metadata } returns TestFactory.createMetadata()
                    // Add any other necessary properties that might be accessed
                }
                
                val params = CreateTableParams(
                    metadata = TestFactory.createMetadata(),
                    name = TestFactory.createValidTableName(),
                    displayName = TestFactory.createValidDisplayName(),
                    description = null,
                    accessLevel = AccessLevel.SYSTEM,
                    columns = listOf(mockColumn)
                )

                // when
                val result = TableDefinition.create(params)

                // then
                result.isRight() shouldBe true
                result.map { table ->
                    table.columns.size shouldBe 1
                    table.columns.first().name shouldBe columnName
                }
            }

            it("should fail with no columns") {
                // given
                val params = CreateTableParams(
                    metadata = TestFactory.createMetadata(),
                    name = TestFactory.createValidTableName(),
                    displayName = TestFactory.createValidDisplayName(),
                    description = null,
                    accessLevel = AccessLevel.SYSTEM,
                    columns = emptyList()
                )

                // when
                val result = TableDefinition.create(params)

                // then
                result.isLeft() shouldBe true
            }
        }
    }
})
