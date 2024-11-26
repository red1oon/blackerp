package org.blackerp.domain.ad.reference

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf
import io.kotest.assertions.arrow.core.shouldBeRight
import org.blackerp.domain.ad.reference.value.ReferenceName
import org.blackerp.domain.values.DisplayName
import org.blackerp.domain.values.Description
import org.blackerp.shared.TestFactory

class ADReferenceTest : DescribeSpec({
    describe("ADReference") {
        it("should create list reference") {
            // given
            val name = ReferenceName.create("test_reference").getOrNull()!!
            val displayName = DisplayName.create("Test Reference").getOrNull()!!
            val description = Description.create("Test reference description").getOrNull()
            
            val params = CreateReferenceParams(
                metadata = TestFactory.createMetadata(),
                name = name,
                displayName = displayName,
                description = description,
                type = ReferenceType.List
            )
            
            // when
            val result = ADReference.create(params)
            
            // then
            result.shouldBeRight().also { reference ->
                reference.name shouldBe name
                reference.displayName shouldBe displayName
                reference.description shouldBe description
                reference.type shouldBe ReferenceType.List
            }
        }

        it("should create table reference") {
            // given
            val name = ReferenceName.create("table_reference").getOrNull()!!
            val displayName = DisplayName.create("Table Reference").getOrNull()!!
            
            val params = CreateReferenceParams(
                metadata = TestFactory.createMetadata(),
                name = name,
                displayName = displayName,
                description = null,
                type = ReferenceType.Table(
                    tableName = "test_table",
                    keyColumn = "id",
                    displayColumn = "name"
                )
            )
            
            // when
            val result = ADReference.create(params)
            
            // then
            result.shouldBeRight().also { reference ->
                val tableRef = reference.type as ReferenceType.Table
                tableRef.tableName shouldBe "test_table"
                tableRef.keyColumn shouldBe "id"
                tableRef.displayColumn shouldBe "name"
            }
        }
    }
})