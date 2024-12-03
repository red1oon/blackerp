package org.blackerp.infrastructure.persistence.repositories

import org.springframework.stereotype.Repository
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.transaction.annotation.Transactional
import org.blackerp.domain.core.ad.table.*
import org.blackerp.domain.core.error.TableError
import arrow.core.Either
import arrow.core.right
import arrow.core.left
import java.util.UUID
import org.slf4j.LoggerFactory
import org.springframework.dao.DataIntegrityViolationException

@Repository
class RelationshipRepository(
    private val jdbcTemplate: JdbcTemplate
) : RelationshipOperations {
    private val logger = LoggerFactory.getLogger(RelationshipRepository::class.java)

    @Transactional
    override suspend fun save(relationship: TableRelationship): Either<TableError, TableRelationship> =
        try {
            logger.debug("Saving relationship: ${relationship.id}")
            
            jdbcTemplate.update("""
                INSERT INTO table_relationship (
                    id, source_table_id, target_table_id, 
                    relationship_type, source_column, target_column,
                    on_delete, on_update
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                ON CONFLICT (id) DO UPDATE SET
                    relationship_type = EXCLUDED.relationship_type,
                    source_column = EXCLUDED.source_column,
                    target_column = EXCLUDED.target_column,
                    on_delete = EXCLUDED.on_delete,
                    on_update = EXCLUDED.on_update
            """,
                relationship.id,
                relationship.sourceTable,
                relationship.targetTable,
                relationship.type.name,
                relationship.sourceColumn,
                relationship.targetColumn,
                relationship.onDelete.name,
                relationship.onUpdate.name
            )
            
            relationship.right()
        } catch (e: DataIntegrityViolationException) {
            logger.error("Constraint violation while saving relationship: ${relationship.id}", e)
            TableError.ValidationError(
                message = "Constraint violation: ${e.message}",
                violations = listOf(TableError.Violation("relationship", "Database constraint violation", null))
            ).left()
        } catch (e: Exception) {
            logger.error("Failed to save relationship: ${relationship.id}", e)
            TableError.DatabaseError(
                message = "Failed to save relationship: ${e.message}",
                sqlState = null,
                errorCode = null
            ).left()
        }

    override suspend fun findBySourceTable(tableId: UUID): Either<TableError, List<TableRelationship>> =
        try {
            val relationships = jdbcTemplate.query("""
                SELECT id, source_table_id, target_table_id, relationship_type,
                       source_column, target_column, on_delete, on_update
                FROM table_relationship
                WHERE source_table_id = ?
            """, { rs, _ ->
                TableRelationship(
                    id = UUID.fromString(rs.getString("id")),
                    sourceTable = UUID.fromString(rs.getString("source_table_id")),
                    targetTable = UUID.fromString(rs.getString("target_table_id")),
                    type = RelationshipType.valueOf(rs.getString("relationship_type")),
                    sourceColumn = rs.getString("source_column"),
                    targetColumn = rs.getString("target_column"),
                    onDelete = CascadeType.valueOf(rs.getString("on_delete")),
                    onUpdate = CascadeType.valueOf(rs.getString("on_update"))
                )
            }, tableId)
            
            relationships.right()
        } catch (e: Exception) {
            logger.error("Failed to find relationships for table: $tableId", e)
            TableError.DatabaseError(
                message = "Failed to find relationships: ${e.message}",
                sqlState = null,
                errorCode = null
            ).left()
        }

    override suspend fun findByTargetTable(tableId: UUID): Either<TableError, List<TableRelationship>> =
        try {
            val relationships = jdbcTemplate.query("""
                SELECT id, source_table_id, target_table_id, relationship_type,
                       source_column, target_column, on_delete, on_update
                FROM table_relationship
                WHERE target_table_id = ?
            """, { rs, _ ->
                TableRelationship(
                    id = UUID.fromString(rs.getString("id")),
                    sourceTable = UUID.fromString(rs.getString("source_table_id")),
                    targetTable = UUID.fromString(rs.getString("target_table_id")),
                    type = RelationshipType.valueOf(rs.getString("relationship_type")),
                    sourceColumn = rs.getString("source_column"),
                    targetColumn = rs.getString("target_column"),
                    onDelete = CascadeType.valueOf(rs.getString("on_delete")),
                    onUpdate = CascadeType.valueOf(rs.getString("on_update"))
                )
            }, tableId)
            
            relationships.right()
        } catch (e: Exception) {
            logger.error("Failed to find relationships for target table: $tableId", e)
            TableError.DatabaseError(
                message = "Failed to find relationships: ${e.message}",
                sqlState = null,
                errorCode = null
            ).left()
        }

    @Transactional
    override suspend fun delete(id: UUID): Either<TableError, Unit> =
        try {
            val deleted = jdbcTemplate.update("DELETE FROM table_relationship WHERE id = ?", id)
            if (deleted > 0) Unit.right()
            else TableError.ValidationError(
                message = "Relationship not found",
                violations = listOf(TableError.Violation("id", "No relationship found with this ID", id))
            ).left()
        } catch (e: Exception) {
            logger.error("Failed to delete relationship: $id", e)
            TableError.DatabaseError(
                message = "Failed to delete relationship: ${e.message}",
                sqlState = null,
                errorCode = null
            ).left()
        }
}
