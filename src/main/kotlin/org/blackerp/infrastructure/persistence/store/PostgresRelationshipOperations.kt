// File: src/main/kotlin/org/blackerp/infrastructure/persistence/store/PostgresRelationshipOperations.kt
package org.blackerp.infrastructure.persistence.store

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import org.blackerp.domain.EntityMetadata
import org.blackerp.domain.table.TableError
import org.blackerp.domain.values.*
import org.blackerp.domain.table.relationship.*
import org.blackerp.domain.table.relationship.value.*
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.time.Instant
import java.util.UUID

@Repository
class PostgresRelationshipOperations(
    private val jdbcTemplate: JdbcTemplate
) : RelationshipOperations {
    private val relationshipMapper = RelationshipRowMapper()

    override suspend fun save(relationship: TableRelationship): Either<TableError, TableRelationship> = try {
        jdbcTemplate.update("""
            INSERT INTO ad_table_relationship (
                id, name, source_table, target_table, type,
                source_column, target_column, delete_rule, update_rule,
                junction_table, created, created_by, updated, updated_by,
                version, active
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """,
            relationship.metadata.id,
            relationship.name.value,
            relationship.sourceTable.value,
            relationship.targetTable.value,
            relationship.type.name,
            relationship.sourceColumn.value,
            relationship.targetColumn.value,
            relationship.deleteRule.name,
            relationship.updateRule.name,
            null, // junction_table is null for non-many-to-many relationships
            relationship.metadata.created,
            relationship.metadata.createdBy,
            relationship.metadata.updated,
            relationship.metadata.updatedBy,
            relationship.metadata.version,
            relationship.metadata.active
        )
        relationship.right()
    } catch (e: DataIntegrityViolationException) {
        when {
            e.message?.contains("uk_relationship_name") == true ->
                TableError.DuplicateTable(relationship.name.value).left()
            else -> TableError.StorageError(e).left()
        }
    } catch (e: Exception) {
        TableError.StorageError(e).left()
    }

    override suspend fun findById(id: UUID): Either<TableError, TableRelationship?> = try {
        val relationship = jdbcTemplate.queryForObject(
            """
            SELECT id, name, source_table, target_table, type,
                   source_column, target_column, delete_rule, update_rule,
                   junction_table, created, created_by, updated, updated_by,
                   version, active
            FROM ad_table_relationship WHERE id = ?
            """,
            relationshipMapper,
            id
        )
        relationship?.right() ?: TableError.NotFound(id.toString()).left()
    } catch (e: EmptyResultDataAccessException) {
        TableError.NotFound(id.toString()).left()
    } catch (e: Exception) {
        TableError.StorageError(e).left()
    }

    override suspend fun findByTable(tableName: TableName): Either<TableError, List<TableRelationship>> = try {
        val relationships = jdbcTemplate.query(
            """
            SELECT id, name, source_table, target_table, type,
                   source_column, target_column, delete_rule, update_rule,
                   junction_table, created, created_by, updated, updated_by,
                   version, active
            FROM ad_table_relationship 
            WHERE source_table = ? OR target_table = ?
            """,
            relationshipMapper,
            tableName.value,
            tableName.value
        )
        relationships.right()
    } catch (e: Exception) {
        TableError.StorageError(e).left()
    }

    override suspend fun delete(id: UUID): Either<TableError, Unit> = try {
        val count = jdbcTemplate.update("DELETE FROM ad_table_relationship WHERE id = ?", id)
        if (count > 0) Unit.right() else TableError.NotFound(id.toString()).left()
    } catch (e: Exception) {
        TableError.StorageError(e).left()
    }

    private class RelationshipRowMapper : RowMapper<TableRelationship> {
        override fun mapRow(rs: ResultSet, rowNum: Int): TableRelationship {
            val metadata = EntityMetadata(
                id = UUID.fromString(rs.getString("id")),
                created = rs.getTimestamp("created").toInstant(),
                createdBy = rs.getString("created_by"),
                updated = rs.getTimestamp("updated").toInstant(),
                updatedBy = rs.getString("updated_by"),
                version = rs.getInt("version"),
                active = rs.getBoolean("active")
            )

            return TableRelationship(
                metadata = metadata,
                name = RelationshipName.create(rs.getString("name")).getOrNull()!!,
                sourceTable = TableName.create(rs.getString("source_table")).getOrNull()!!,
                targetTable = TableName.create(rs.getString("target_table")).getOrNull()!!,
                type = RelationType.valueOf(rs.getString("type")),
                sourceColumn = ColumnName.create(rs.getString("source_column")).getOrNull()!!,
                targetColumn = ColumnName.create(rs.getString("target_column")).getOrNull()!!,
                constraints = emptyList(), // Load constraints if needed
                deleteRule = DeleteRule.valueOf(rs.getString("delete_rule")),
                updateRule = UpdateRule.valueOf(rs.getString("update_rule"))
            )
        }
    }
}