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
class ConstraintRepository(
    private val jdbcTemplate: JdbcTemplate
) : ConstraintOperations {
    private val logger = LoggerFactory.getLogger(ConstraintRepository::class.java)

    @Transactional
    override suspend fun save(constraint: TableConstraint): Either<TableError, TableConstraint> =
        try {
            logger.debug("Saving constraint: ${constraint.id}")
            
            // Save main constraint
            jdbcTemplate.update("""
                INSERT INTO table_constraint (
                    id, table_id, name, constraint_type, expression
                ) VALUES (?, ?, ?, ?, ?)
                ON CONFLICT (id) DO UPDATE SET
                    name = EXCLUDED.name,
                    constraint_type = EXCLUDED.constraint_type,
                    expression = EXCLUDED.expression
            """,
                constraint.id,
                constraint.tableId,
                constraint.name,
                constraint.type.name,
                constraint.expression
            )

            // Save constraint columns
            saveConstraintColumns(constraint)
            
            constraint.right()
        } catch (e: DataIntegrityViolationException) {
            logger.error("Constraint violation while saving constraint: ${constraint.id}", e)
            TableError.ValidationError(
                message = "Constraint violation: ${e.message}",
                violations = listOf(TableError.Violation("constraint", "Database constraint violation", null))
            ).left()
        } catch (e: Exception) {
            logger.error("Failed to save constraint: ${constraint.id}", e)
            TableError.DatabaseError(
                message = "Failed to save constraint: ${e.message}",
                sqlState = null,
                errorCode = null
            ).left()
        }

    private fun saveConstraintColumns(constraint: TableConstraint) {
        // First delete existing columns
        jdbcTemplate.update(
            "DELETE FROM table_constraint_column WHERE constraint_id = ?",
            constraint.id
        )

        // Then insert new columns
        constraint.columns.forEach { column ->
            jdbcTemplate.update("""
                INSERT INTO table_constraint_column (constraint_id, column_name)
                VALUES (?, ?)
            """,
                constraint.id,
                column
            )
        }
    }

    override suspend fun findByTable(tableId: UUID): Either<TableError, List<TableConstraint>> =
        try {
            val constraints = jdbcTemplate.query("""
                SELECT c.id, c.table_id, c.name, c.constraint_type, c.expression,
                       array_agg(cc.column_name) as columns
                FROM table_constraint c
                LEFT JOIN table_constraint_column cc ON c.id = cc.constraint_id
                WHERE c.table_id = ?
                GROUP BY c.id, c.table_id, c.name, c.constraint_type, c.expression
            """, { rs, _ ->
                TableConstraint(
                    id = UUID.fromString(rs.getString("id")),
                    tableId = UUID.fromString(rs.getString("table_id")),
                    name = rs.getString("name"),
                    type = ConstraintType.valueOf(rs.getString("constraint_type")),
                    columns = (rs.getArray("columns")?.array as Array<String>?)?.toList() ?: emptyList(),
                    expression = rs.getString("expression")
                )
            }, tableId)
            
            constraints.right()
        } catch (e: Exception) {
            logger.error("Failed to find constraints for table: $tableId", e)
            TableError.DatabaseError(
                message = "Failed to find constraints: ${e.message}",
                sqlState = null,
                errorCode = null
            ).left()
        }

    @Transactional
    override suspend fun delete(id: UUID): Either<TableError, Unit> =
        try {
            // First delete constraint columns
            jdbcTemplate.update("DELETE FROM table_constraint_column WHERE constraint_id = ?", id)
            
            // Then delete the constraint
            val deleted = jdbcTemplate.update("DELETE FROM table_constraint WHERE id = ?", id)
            
            if (deleted > 0) Unit.right()
            else TableError.ValidationError(
                message = "Constraint not found",
                violations = listOf(TableError.Violation("id", "No constraint found with this ID", id))
            ).left()
        } catch (e: Exception) {
            logger.error("Failed to delete constraint: $id", e)
            TableError.DatabaseError(
                message = "Failed to delete constraint: ${e.message}",
                sqlState = null,
                errorCode = null
            ).left()
        }

    override suspend fun validateConstraint(constraint: TableConstraint): Either<TableError, Unit> =
        when (constraint.type) {
            ConstraintType.UNIQUE -> validateUniqueConstraint(constraint)
            ConstraintType.CHECK -> validateCheckConstraint(constraint)
            ConstraintType.FOREIGN_KEY -> validateForeignKeyConstraint(constraint)
        }

    private suspend fun validateUniqueConstraint(constraint: TableConstraint): Either<TableError, Unit> =
        try {
            val count = jdbcTemplate.queryForObject("""
                SELECT COUNT(*) FROM (
                    SELECT ${constraint.columns.joinToString(", ")}
                    FROM ${getTableName(constraint.tableId)}
                    GROUP BY ${constraint.columns.joinToString(", ")}
                    HAVING COUNT(*) > 1
                ) t
            """, Int::class.java)
            
            if (count == 0) Unit.right()
            else TableError.ValidationError(
                message = "Unique constraint violation",
                violations = listOf(TableError.Violation("constraint", "Duplicate values found", constraint.name))
            ).left()
        } catch (e: Exception) {
            logger.error("Failed to validate unique constraint: ${constraint.id}", e)
            TableError.DatabaseError(
                message = "Failed to validate constraint: ${e.message}",
                sqlState = null,
                errorCode = null
            ).left()
        }

    private suspend fun validateCheckConstraint(constraint: TableConstraint): Either<TableError, Unit> =
        try {
            // Validate the check constraint expression
            jdbcTemplate.queryForObject("""
                SELECT COUNT(*) FROM ${getTableName(constraint.tableId)}
                WHERE NOT (${constraint.expression})
            """, Int::class.java)?.let { violations ->
                if (violations == 0) Unit.right()
                else TableError.ValidationError(
                    message = "Check constraint violation",
                    violations = listOf(TableError.Violation("constraint", "$violations rows violate the check condition", constraint.name))
                ).left()
            } ?: Unit.right()
        } catch (e: Exception) {
            logger.error("Failed to validate check constraint: ${constraint.id}", e)
            TableError.DatabaseError(
                message = "Failed to validate constraint: ${e.message}",
                sqlState = null,
                errorCode = null
            ).left()
        }

    private suspend fun validateForeignKeyConstraint(constraint: TableConstraint): Either<TableError, Unit> =
        try {
            // Implementation depends on your specific foreign key validation requirements
            Unit.right()
        } catch (e: Exception) {
            logger.error("Failed to validate foreign key constraint: ${constraint.id}", e)
            TableError.DatabaseError(
                message = "Failed to validate constraint: ${e.message}",
                sqlState = null,
                errorCode = null
            ).left()
        }

    private fun getTableName(tableId: UUID): String =
        jdbcTemplate.queryForObject(
            "SELECT name FROM ad_table WHERE id = ?",
            String::class.java,
            tableId
        ) ?: throw IllegalStateException("Table not found: $tableId")
}
