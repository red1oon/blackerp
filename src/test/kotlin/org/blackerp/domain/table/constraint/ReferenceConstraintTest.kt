package org.blackerp.domain.table.constraint

import arrow.core.right
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.blackerp.domain.table.*
import org.blackerp.domain.table.definition.TableDefinition
import org.blackerp.domain.values.*
import org.blackerp.shared.TestFactory
import org.blackerp.shared.ReferenceValidation

class ReferenceConstraintTest : DescribeSpec({
    describe("validate") {
        it("should fail when source column does not exist") {
            runTest {
                // given
                val mockTableOps = mockk<TableOperations>()
                val sourceTableDef = mockk<TableDefinition>()
                val columnName = ColumnName.create("test_column").getOrNull()!!
                val refTableName = TableName.create("ref_table").getOrNull()!!
                val refColumnName = ColumnName.create("ref_column").getOrNull()!!

                every { sourceTableDef.columns } returns emptyList()

                val constraint = ReferenceConstraint(
                    metadata = TestFactory.createMetadata(),
                    column = columnName,
                    referenceTable = refTableName,
                    referenceColumn = refColumnName,
                    tableOperations = mockTableOps
                )

                // when
                val result = constraint.validate(sourceTableDef)

                // then
                result.isLeft() shouldBe true
                result.fold(
                    { error -> error.shouldBeTypeOf<ReferenceValidation.ColumnNotFound>() },
                    { throw AssertionError("Should not succeed") }
                )
            }
        }

        it("should fail when reference table not found") {
            runTest {
                // given
                val mockTableOps = mockk<TableOperations>()
                val sourceTableDef = mockk<TableDefinition>()
                val columnName = ColumnName.create("test_column").getOrNull()!!
                val refTableName = TableName.create("ref_table").getOrNull()!!
                val refColumnName = ColumnName.create("ref_column").getOrNull()!!

                // Set up source column to exist
                val sourceColumn = mockk<ColumnDefinition>()
                every { sourceColumn.name } returns columnName
                every { sourceColumn.dataType } returns DataType.STRING

                every { sourceTableDef.columns } returns listOf(sourceColumn)

                // Set up reference table to not be found
                coEvery { mockTableOps.findByName(refTableName.value) } returns null.right()

                val constraint = ReferenceConstraint(
                    metadata = TestFactory.createMetadata(),
                    column = columnName,
                    referenceTable = refTableName,
                    referenceColumn = refColumnName,
                    tableOperations = mockTableOps
                )

                // when
                val result = constraint.validate(sourceTableDef)

                // then
                result.isLeft() shouldBe true
                result.fold(
                    { error -> error.shouldBeTypeOf<ReferenceValidation.ReferenceTableNotFound>() },
                    { throw AssertionError("Should not succeed") }
                )
            }
        }

        it("should fail when column types are incompatible") {
            runTest {
                // given
                val mockTableOps = mockk<TableOperations>()
                val sourceTableDef = mockk<TableDefinition>()
                val columnName = ColumnName.create("test_column").getOrNull()!!
                val refTableName = TableName.create("ref_table").getOrNull()!!
                val refColumnName = ColumnName.create("ref_column").getOrNull()!!

                // Source column with STRING type
                val sourceColumn = mockk<ColumnDefinition>()
                every { sourceColumn.name } returns columnName
                every { sourceColumn.dataType } returns DataType.STRING

                every { sourceTableDef.columns } returns listOf(sourceColumn)

                // Reference table setup
                val refTable = mockk<ADTable>()
                val refColumn = mockk<ColumnDefinition>()
                every { refColumn.name } returns refColumnName
                every { refColumn.dataType } returns DataType.INTEGER // Intentionally incompatible type

                every { refTable.columns } returns listOf(refColumn)
                every { refTable.metadata } returns TestFactory.createMetadata()
                every { refTable.name } returns refTableName
                every { refTable.displayName } returns DisplayName.create("Reference Table").getOrNull()!!
                every { refTable.description } returns null
                every { refTable.accessLevel } returns AccessLevel.SYSTEM

                // Set up TableOperations to return our mock table
                coEvery { mockTableOps.findByName(refTableName.value) } returns refTable.right()

                val constraint = ReferenceConstraint(
                    metadata = TestFactory.createMetadata(),
                    column = columnName,
                    referenceTable = refTableName,
                    referenceColumn = refColumnName,
                    tableOperations = mockTableOps
                )

                // when
                val result = constraint.validate(sourceTableDef)

                // then
                result.isLeft() shouldBe true
                result.fold(
                    { error -> error.shouldBeTypeOf<ReferenceValidation.IncompatibleTypes>() },
                    { throw AssertionError("Should not succeed") }
                )
            }
        }
    }
})
