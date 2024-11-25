package org.blackerp.domain.table.relationship

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import org.blackerp.domain.values.TableName
import org.blackerp.domain.values.ColumnName
import org.blackerp.domain.table.relationship.value.RelationType
import org.blackerp.domain.table.relationship.value.RelationshipName
import org.blackerp.shared.TestFactory

class TableRelationshipTest : DescribeSpec({
    describe("TableRelationship") {
        context("creation") {
            it("should create valid relationship") {
                // Create test parameters
                val params = CreateRelationshipParams(
                    metadata = TestFactory.createMetadata(),
                    name = RelationshipName.create("test_relation").getOrNull()!!,
                    sourceTable = TableName.create("source_table").getOrNull()!!,
                    targetTable = TableName.create("target_table").getOrNull()!!,
                    type = RelationType.ONE_TO_MANY,
                    sourceColumn = ColumnName.create("source_id").getOrNull()!!,
                    targetColumn = ColumnName.create("target_id").getOrNull()!!
                )

                // Create relationship
                val result = TableRelationship.create(params)

                // Verify
                result.isRight() shouldBe true
                result.map { relationship ->
                    relationship.name.value shouldBe "test_relation"
                    relationship.type shouldBe RelationType.ONE_TO_MANY
                }
            }
        }
    }
})
