package org.blackerp.infrastructure.persistence.store

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import org.blackerp.domain.EntityMetadata
import org.blackerp.domain.ad.tab.*
import org.blackerp.domain.ad.tab.value.TabName
import org.blackerp.domain.values.*
import org.blackerp.domain.table.TableOperations
import org.blackerp.shared.ValidationError
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.time.Instant
import java.util.UUID
import java.io.File

@Repository
class PostgresTabOperations(
    private val jdbcTemplate: JdbcTemplate,
    private val tableOperations: TableOperations
) : TabOperations {

    private fun debug(msg: String) {
        File("testdebug.txt").appendText("${Instant.now()}: $msg\n")
    }
    
    override suspend fun save(tab: ADTab): Either<TabError, ADTab> = 
        Either.catch {
            debug("Saving tab: ${tab.name.value}")
            
            // First, verify the table exists
            val tableExists = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM ad_table WHERE name = ?",
                Int::class.java,
                tab.table.name.value
            ) ?: 0
            
            debug("Table exists check: $tableExists")
            if (tableExists == 0) {
                throw IllegalStateException("Referenced table ${tab.table.name.value} does not exist")
            }

            // Insert/Update tab using MERGE
            val mergeResult = jdbcTemplate.update("""
                MERGE INTO ad_tab KEY(id) VALUES (
                    ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?
                )
                """,
                tab.metadata.id,
                tab.name.value,
                tab.displayName.value,
                tab.description?.value,
                tab.table.name.value,
                tab.metadata.created,
                tab.metadata.createdBy,
                tab.metadata.updated,
                tab.metadata.updatedBy,
                tab.metadata.version,
                tab.metadata.active
            )
            debug("Tab merge result: $mergeResult")

            // Update query columns
            jdbcTemplate.update("DELETE FROM ad_tab_query_column WHERE tab_id = ?", tab.metadata.id)
            tab.queryColumns.forEachIndexed { index, column ->
                val qcResult = jdbcTemplate.update("""
                    INSERT INTO ad_tab_query_column (tab_id, column_name, sequence)
                    VALUES (?, ?, ?)
                    """,
                    tab.metadata.id,
                    column.value,
                    index
                )
                debug("Query column insert result: $qcResult")
            }

            // Update display columns
            jdbcTemplate.update("DELETE FROM ad_tab_display_column WHERE tab_id = ?", tab.metadata.id)
            tab.displayColumns.forEachIndexed { index, column ->
                val dcResult = jdbcTemplate.update("""
                    INSERT INTO ad_tab_display_column (tab_id, column_name, sequence)
                    VALUES (?, ?, ?)
                    """,
                    tab.metadata.id,
                    column.value,
                    index
                )
                debug("Display column insert result: $dcResult")
            }

            // Update order by specifications
            jdbcTemplate.update("DELETE FROM ad_tab_order_by WHERE tab_id = ?", tab.metadata.id)
            tab.orderBy.forEachIndexed { index, spec ->
                val obResult = jdbcTemplate.update("""
                    INSERT INTO ad_tab_order_by (tab_id, column_name, direction, sequence)
                    VALUES (?, ?, ?, ?)
                    """,
                    tab.metadata.id,
                    spec.column.value,
                    spec.direction.name,
                    index
                )
                debug("Order by insert result: $obResult")
            }

            tab
        }.mapLeft { e -> 
            debug("Error saving tab: ${e.message}")
            e.printStackTrace(File("testdebug.txt").printWriter())
            when (e) {
                is IllegalStateException -> TabError.ValidationFailed(
                    listOf(ValidationError.InvalidValue(e.message ?: "Unknown error"))
                )
                else -> TabError.ValidationFailed(
                    listOf(ValidationError.InvalidValue(e.message ?: "Unknown error"))
                )
            }
        }

    override suspend fun findById(id: UUID): Either<TabError, ADTab?> =
        Either.catch {
            val results = jdbcTemplate.query(
                """
                SELECT t.*, 
                       array_agg(DISTINCT qc.column_name) as query_columns,
                       array_agg(DISTINCT dc.column_name) as display_columns,
                       array_agg(DISTINCT ob.column_name || ',' || ob.direction) as order_by
                FROM ad_tab t
                LEFT JOIN ad_tab_query_column qc ON t.id = qc.tab_id
                LEFT JOIN ad_tab_display_column dc ON t.id = dc.tab_id
                LEFT JOIN ad_tab_order_by ob ON t.id = ob.tab_id
                WHERE t.id = ?
                GROUP BY t.id, t.name, t.display_name, t.description, t.table_name,
                         t.created, t.created_by, t.updated, t.updated_by, t.version, t.active
                """,
                TabRowMapper(tableOperations),
                id
            )
            
            results.firstOrNull()
        }.mapLeft { e -> 
            TabError.ValidationFailed(
                listOf(ValidationError.InvalidValue(e.message ?: "Unknown error"))
            )
        }

    override suspend fun findByTable(tableName: TableName): Either<TabError, List<ADTab>> =
        Either.catch {
            jdbcTemplate.query(
                """
                SELECT t.*, 
                       array_agg(DISTINCT qc.column_name) as query_columns,
                       array_agg(DISTINCT dc.column_name) as display_columns,
                       array_agg(DISTINCT ob.column_name || ',' || ob.direction) as order_by
                FROM ad_tab t
                LEFT JOIN ad_tab_query_column qc ON t.id = qc.tab_id
                LEFT JOIN ad_tab_display_column dc ON t.id = dc.tab_id
                LEFT JOIN ad_tab_order_by ob ON t.id = ob.tab_id
                WHERE t.table_name = ?
                GROUP BY t.id, t.name, t.display_name, t.description, t.table_name,
                         t.created, t.created_by, t.updated, t.updated_by, t.version, t.active
                """,
                TabRowMapper(tableOperations),
                tableName.value
            )
        }.mapLeft { e -> 
            TabError.ValidationFailed(
                listOf(ValidationError.InvalidValue(e.message ?: "Unknown error"))
            )
        }

    override suspend fun delete(id: UUID): Either<TabError, Unit> =
        Either.catch {
            val count = jdbcTemplate.update("DELETE FROM ad_tab WHERE id = ?", id)
            if (count == 0) throw IllegalStateException("Tab not found: $id")
            Unit
        }.mapLeft { e -> 
            TabError.ValidationFailed(
                listOf(ValidationError.InvalidValue(e.message ?: "Unknown error"))
            )
        }

    private class TabRowMapper(
        private val tableOperations: TableOperations
    ) : RowMapper<ADTab> {
        override fun mapRow(rs: ResultSet, rowNum: Int): ADTab {
            val metadata = EntityMetadata(
                id = UUID.fromString(rs.getString("id")),
                created = rs.getTimestamp("created").toInstant(),
                createdBy = rs.getString("created_by"),
                updated = rs.getTimestamp("updated").toInstant(),
                updatedBy = rs.getString("updated_by"),
                version = rs.getInt("version"),
                active = rs.getBoolean("active")
            )

            val tableName = rs.getString("table_name")
            val table = kotlinx.coroutines.runBlocking { 
                tableOperations.findByName(tableName).getOrNull()
                    ?: throw IllegalStateException("Table not found: $tableName")
            }

            // Safe array handling
            val queryColumns = (rs.getArray("query_columns")?.array as? Array<*>)
                ?.filterNotNull()
                ?.map { it.toString() }
                ?.map { ColumnName.create(it).getOrNull()!! }
                ?: emptyList()

            val displayColumns = (rs.getArray("display_columns")?.array as? Array<*>)
                ?.filterNotNull()
                ?.map { it.toString() }
                ?.map { ColumnName.create(it).getOrNull()!! }
                ?: emptyList()

            val orderBy = (rs.getArray("order_by")?.array as? Array<*>)
                ?.filterNotNull()
                ?.map { it.toString() }
                ?.map { spec -> 
                    val (col, dir) = spec.split(",")
                    OrderBySpec(
                        column = ColumnName.create(col).getOrNull()!!,
                        direction = SortDirection.valueOf(dir)
                    )
                }
                ?: emptyList()

            return ADTab(
                metadata = metadata,
                name = TabName.create(rs.getString("name")).getOrNull()!!,
                displayName = DisplayName.create(rs.getString("display_name")).getOrNull()!!,
                description = rs.getString("description")?.let { 
                    Description.create(it).getOrNull()
                },
                table = table,
                queryColumns = queryColumns,
                displayColumns = displayColumns,
                orderBy = orderBy
            )
        }
    }
}