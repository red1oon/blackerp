package org.blackerp.infrastructure.persistence.store

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import org.blackerp.domain.EntityMetadata
import org.blackerp.domain.table.ADTable
import org.blackerp.domain.table.ColumnDefinition
import org.blackerp.domain.table.TableError
import org.blackerp.domain.table.TableOperations
import org.blackerp.domain.values.*
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.time.Instant
import java.util.UUID

@Repository
class PostgresTableOperations(
    private val jdbcTemplate: JdbcTemplate
) : TableOperations {
    private val tableMapper = TableRowMapper()

    override suspend fun findAll(): Either<TableError, List<ADTable>> = try {
        val tables = jdbcTemplate.query(
            """
            SELECT id, name, display_name, description, access_level,
                created, created_by, updated, updated_by, version, active
            FROM ad_table
            WHERE active = true
            """,
            tableMapper
        )
        tables.right()
    } catch (e: Exception) {
        TableError.StorageError(e).left()
    }

    override suspend fun save(table: ADTable): Either<TableError, ADTable> = try {
        jdbcTemplate.update("""
            INSERT INTO ad_table (
                id, name, display_name, description, access_level,
                created, created_by, updated, updated_by, version, active
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """,
            table.metadata.id,
            table.name.value,
            table.displayName.value,
            table.description?.value,
            table.accessLevel.name,
            table.metadata.created,
            table.metadata.createdBy,
            table.metadata.updated,
            table.metadata.updatedBy,
            table.metadata.version,
            table.metadata.active
        )
        table.right()
    } catch (e: DataIntegrityViolationException) {
        when {
            e.message?.contains("name_format") == true ->
                TableError.StorageError(e).left()
            e.message?.contains("unique") == true ->
                TableError.DuplicateTable(table.name.value).left()
            else -> TableError.StorageError(e).left()
        }
    } catch (e: Exception) {
        TableError.StorageError(e).left()
    }

    override suspend fun findById(id: UUID): Either<TableError, ADTable?> = try {
        val table = jdbcTemplate.queryForObject(
            """
            SELECT id, name, display_name, description, access_level,
                   created, created_by, updated, updated_by, version, active
            FROM ad_table WHERE id = ?
            """,
            tableMapper,
            id
        )
        table?.right() ?: TableError.NotFound(id.toString()).left()
    } catch (e: EmptyResultDataAccessException) {
        TableError.NotFound(id.toString()).left()
    } catch (e: Exception) {
        TableError.StorageError(e).left()
    }

    override suspend fun findByName(name: String): Either<TableError, ADTable?> = try {
        val table = jdbcTemplate.queryForObject(
            """
            SELECT id, name, display_name, description, access_level,
                   created, created_by, updated, updated_by, version, active
            FROM ad_table WHERE name = ?
            """,
            tableMapper,
            name
        )
        table?.right() ?: TableError.NotFound(name).left()
    } catch (e: EmptyResultDataAccessException) {
        TableError.NotFound(name).left()
    } catch (e: Exception) {
        TableError.StorageError(e).left()
    }

    override suspend fun delete(id: UUID): Either<TableError, Unit> = try {
        val count = jdbcTemplate.update("DELETE FROM ad_table WHERE id = ?", id)
        if (count > 0) Unit.right() else TableError.NotFound(id.toString()).left()
    } catch (e: Exception) {
        TableError.StorageError(e).left()
    }

    private class TableRowMapper : RowMapper<ADTable> {
        override fun mapRow(rs: ResultSet, rowNum: Int): ADTable {
            val metadata = EntityMetadata(
                id = UUID.fromString(rs.getString("id")),
                created = rs.getTimestamp("created").toInstant(),
                createdBy = rs.getString("created_by"),
                updated = rs.getTimestamp("updated").toInstant(),
                updatedBy = rs.getString("updated_by"),
                version = rs.getInt("version"),
                active = rs.getBoolean("active")
            )

            // Placeholder for fetching columns, replace with actual logic to retrieve columns from database
            val columns = listOf<ColumnDefinition>()

            return ADTable(
                metadata = metadata,
                name = TableName.create(rs.getString("name")).getOrNull()!!,
                displayName = DisplayName.create(rs.getString("display_name")).getOrNull()!!,
                description = rs.getString("description")?.let { 
                    Description.create(it).getOrNull()
                },
                accessLevel = AccessLevel.fromString(rs.getString("access_level")),
                columns = columns // Pass columns here
            )
        }
    }
}
