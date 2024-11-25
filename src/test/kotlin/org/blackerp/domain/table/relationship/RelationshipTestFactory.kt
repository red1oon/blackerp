// File: src/test/kotlin/org/blackerp/domain/table/relationship/RelationshipTestFactory.kt
package org.blackerp.domain.table.relationship

import org.blackerp.domain.EntityMetadata
import org.blackerp.domain.values.TableName
import org.blackerp.domain.values.ColumnName
import org.blackerp.domain.table.relationship.value.*
import org.blackerp.domain.table.relationship.constraint.IndexConstraint
import org.blackerp.shared.TestFactory
import org.blackerp.domain.table.relationship.value.DeleteRule
import org.blackerp.domain.table.relationship.value.UpdateRule

object RelationshipTestFactory {
    fun createValidRelationship(): TableRelationship {
        val metadata = TestFactory.createMetadata()
        return TableRelationship(
            metadata = metadata,
            name = createValidRelationshipName(),
            sourceTable = createValidTableName("source_table"),
            targetTable = createValidTableName("target_table"),
            type = RelationType.ONE_TO_MANY,
            sourceColumn = createValidColumnName("source_id"),
            targetColumn = createValidColumnName("target_id"),
            constraints = listOf(createValidIndexConstraint("target_id")),
            deleteRule = DeleteRule.RESTRICT,
            updateRule = UpdateRule.RESTRICT
        )
    }

    fun createValidRelationshipParams(): CreateRelationshipParams {
        return CreateRelationshipParams(
            metadata = TestFactory.createMetadata(),
            name = createValidRelationshipName(),
            sourceTable = createValidTableName("source_table"),
            targetTable = createValidTableName("target_table"),
            type = RelationType.ONE_TO_MANY,
            sourceColumn = createValidColumnName("source_id"),
            targetColumn = createValidColumnName("target_id"),
            constraints = listOf(createValidIndexConstraint("target_id")),
            deleteRule = DeleteRule.RESTRICT,
            updateRule = UpdateRule.RESTRICT
        )
    }

    private fun createValidRelationshipName(): RelationshipName =
        RelationshipName.create("test_relation").getOrNull()
            ?: throw IllegalStateException("Failed to create valid relationship name")

    private fun createValidTableName(name: String): TableName =
        TableName.create(name).getOrNull()
            ?: throw IllegalStateException("Failed to create valid table name: $name")

    private fun createValidColumnName(name: String): ColumnName =
        ColumnName.create(name).getOrNull()
            ?: throw IllegalStateException("Failed to create valid column name: $name")

    private fun createValidIndexConstraint(columnName: String): IndexConstraint {
        val column = createValidColumnName(columnName)
        return IndexConstraint(
            metadata = TestFactory.createMetadata(),
            columns = listOf(column)
        )
    }

    fun createManyToManyParams(): CreateRelationshipParams {
        return CreateRelationshipParams(
            metadata = TestFactory.createMetadata(),
            name = createValidRelationshipName(),
            sourceTable = createValidTableName("table_a"),
            targetTable = createValidTableName("table_b"),
            type = RelationType.MANY_TO_MANY,
            sourceColumn = createValidColumnName("a_id"),
            targetColumn = createValidColumnName("b_id"),
            junctionTable = createValidTableName("junction_table")
        )
    }

    fun createSelfReferenceParams(): CreateRelationshipParams {
        val tableName = createValidTableName("self_ref_table")
        return CreateRelationshipParams(
            metadata = TestFactory.createMetadata(),
            name = createValidRelationshipName(),
            sourceTable = tableName,
            targetTable = tableName,
            type = RelationType.ONE_TO_MANY,
            sourceColumn = createValidColumnName("parent_id"),
            targetColumn = createValidColumnName("child_id"),
            constraints = listOf(createValidIndexConstraint("child_id"))
        )
    }
}