
package org.blackerp.domain.table.constraint

import arrow.core.right
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.blackerp.domain.EntityMetadata
import org.blackerp.domain.table.ColumnDefinition
import org.blackerp.domain.table.definition.TableDefinition
import org.blackerp.domain.values.ColumnName
import org.blackerp.shared.TestFactory
import java.time.Instant  // Required by EntityMetadata
import java.util.UUID    // Required by EntityMetadata

class UniqueConstraintTest : DescribeSpec({
    describe("UniqueConstraint") { 
        
        it("should validate when all columns exist") {
            // given
            val columnName = ColumnName.create("test_column").getOrNull()!!
            val mockColumn = mockk<ColumnDefinition>()
            every { mockColumn.name } returns columnName
            
            val mockTable = mockk<TableDefinition>()
            every { mockTable.columns } returns listOf(mockColumn)
            
            val constraint = UniqueConstraint(
                metadata = TestFactory.createMetadata(),
                columns = listOf(columnName)
            )
            
            // when
            val result = constraint.validate(mockTable)
            
            // then
            result.isRight() shouldBe true
        }
        
        it("should fail when columns dont exist") {
            // given
            val columnName = ColumnName.create("test_column").getOrNull()!!
            val mockTable = mockk<TableDefinition>()
            every { mockTable.columns } returns emptyList()
            
            val constraint = UniqueConstraint(
                metadata = TestFactory.createMetadata(),
                columns = listOf(columnName)
            )
            
            // when
            val result = constraint.validate(mockTable)
            
            // then
            result.isLeft() shouldBe true
        }
    }
})

