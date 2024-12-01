package org.blackerp.domain.table.relationship

import org.blackerp.domain.EntityMetadata
import org.blackerp.domain.values.TableName
import org.blackerp.domain.values.ColumnName
import org.blackerp.domain.table.relationship.value.RelationType
import org.blackerp.domain.table.relationship.value.RelationshipName
import org.blackerp.domain.table.relationship.value.DeleteRule
import org.blackerp.domain.table.relationship.value.UpdateRule

data class CreateRelationshipParams(
    val metadata: EntityMetadata,
    val name: RelationshipName,
    val sourceTable: TableName,
    val targetTable: TableName,
    val type: RelationType,
    val sourceColumn: ColumnName,
    val targetColumn: ColumnName,
    val deleteRule: DeleteRule = DeleteRule.RESTRICT,    // Added this
    val updateRule: UpdateRule = UpdateRule.RESTRICT,    // Added this
    val junctionTable: TableName? = null, 
    val constraints: List<RelationshipConstraint> = emptyList()
)
