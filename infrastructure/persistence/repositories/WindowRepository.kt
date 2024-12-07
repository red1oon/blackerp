package org.blackerp.infrastructure.persistence.repositories

import org.springframework.stereotype.Repository
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.transaction.annotation.Transactional
import org.blackerp.domain.core.ad.window.*
import org.blackerp.domain.core.ad.tab.ADTab
import org.blackerp.domain.core.shared.EntityMetadata
import org.blackerp.domain.core.shared.AuditInfo
import org.blackerp.domain.core.shared.VersionInfo
import org.blackerp.domain.core.values.*
import arrow.core.Either
import arrow.core.right
import arrow.core.left
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.UUID
import org.slf4j.LoggerFactory

@Repository
class WindowRepositoryImpl(
    private val jdbcTemplate: JdbcTemplate
) : WindowRepository {
    private val logger = LoggerFactory.getLogger(WindowRepositoryImpl::class.java)

    @Transactional
    override suspend fun save(window: ADWindow): Either<WindowError, ADWindow> = try {
        logger.debug("Saving window: ${window.id}")
        
        jdbcTemplate.update("""
            INSERT INTO ad_window (
                id, name, display_name, description, 
                is_active, is_sotrx, window_type
            ) VALUES (?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT (id) DO UPDATE SET
                name = EXCLUDED.name,
                display_name = EXCLUDED.display_name,
                description = EXCLUDED.description,
                is_active = EXCLUDED.is_active,
                is_sotrx = EXCLUDED.is_sotrx,
                window_type = EXCLUDED.window_type
        """,
            window.id,
            window.name.value,
            window.displayName.value,
            window.description?.value,
            window.isActive,
            window.isSOTrx,
            window.windowType.name
        )

        saveTabs(window)
        window.right()
    } catch (e: Exception) {
        logger.error("Failed to save window: ${window.id}", e)
        WindowError.ValidationFailed("Save failed: ${e.message}").left()
    }

    private fun saveTabs(window: ADWindow) {
        // Delete existing tabs
        jdbcTemplate.update("DELETE FROM ad_tab WHERE window_id = ?", window.id)

        window.tabs.forEach { tab ->
            // Save tab
            jdbcTemplate.update("""
                INSERT INTO ad_tab (
                    id, window_id, name, display_name,
                    description, table_id, sequence
                ) VALUES (?, ?, ?, ?, ?, ?, ?)
            """,
                tab.id,
                window.id,
                tab.name.value,
                tab.displayName.value,
                tab.description?.value,
                tab.tableId,
                tab.sequence
            )

            // Save tab fields
            saveTabFields(tab)
        }
    }

    private fun findFieldsByTabId(tabId: UUID): List<WindowField> {
        return jdbcTemplate.query(
            "SELECT * FROM ad_field WHERE tab_id = ? ORDER BY sequence",
            { rs, _ ->
                WindowField(
                    id = UUID.fromString(rs.getString("id")),
                    columnName = rs.getString("column_name"),
                    displayName = rs.getString("display_name"),
                    description = rs.getString("description"),
                    isDisplayed = rs.getBoolean("is_displayed"),
                    isReadOnly = rs.getBoolean("is_readonly"),
                    isMandatory = rs.getBoolean("is_mandatory"),
                    sequence = rs.getInt("sequence"),
                    displayLogic = rs.getString("display_logic"),
                    defaultValue = rs.getString("default_value"),
                    validationRule = rs.getString("validation_rule")
                )
            },
            tabId
        )
    }

    private fun saveTabFields(tab: ADTab) {
        // Delete existing fields
        jdbcTemplate.update("DELETE FROM ad_field WHERE tab_id = ?", tab.id)

        tab.fields.forEach { field ->
            jdbcTemplate.update("""
                INSERT INTO ad_field (
                    id, tab_id, column_name, display_name,
                    description, is_displayed, is_readonly,
                    is_mandatory, sequence, display_logic,
                    default_value, validation_rule
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """,
                field.id,
                tab.id,
                field.columnName,
                field.displayName,
                field.description,
                field.isDisplayed,
                field.isReadOnly,
                field.isMandatory,
                field.sequence,
                field.displayLogic,
                field.defaultValue,
                field.validationRule
            )
        }
    }

    @Transactional
    override suspend fun delete(id: UUID): Either<WindowError, Unit> = try {
        val deleted = jdbcTemplate.update("DELETE FROM ad_window WHERE id = ?", id)
        if (deleted > 0) Unit.right() 
        else WindowError.NotFound(id).left()
    } catch (e: Exception) {
        logger.error("Failed to delete window: $id", e)
        WindowError.ValidationFailed("Delete failed: ${e.message}").left()
    }

    override suspend fun findById(id: UUID): Either<WindowError, ADWindow?> = try {
        val window = jdbcTemplate.queryForObject(
            """
            SELECT w.*, 
                u1.username as created_by, 
                u2.username as updated_by,
                w.version 
            FROM ad_window w 
            LEFT JOIN ad_user u1 ON w.created_by = u1.id
            LEFT JOIN ad_user u2 ON w.updated_by = u2.id
            WHERE w.id = ?
            """,
            { rs, _ ->
                ADWindow(
                    metadata = EntityMetadata(
                        id = rs.getString("id"),
                        audit = AuditInfo(
                            createdBy = rs.getString("created_by"),
                            updatedBy = rs.getString("updated_by")
                        ),
                        version = VersionInfo(version = rs.getInt("version"))
                    ),
                    displayName = DisplayName.create(rs.getString("display_name")).orNull()!!,
                    name = WindowName.create(rs.getString("name")).orNull()!!,
                    description = rs.getString("description")?.let { 
                        Description.create(it).orNull() 
                    },
                    tabs = findTabsByWindowId(UUID.fromString(rs.getString("id"))),
                    isActive = rs.getBoolean("is_active"),
                    isSOTrx = rs.getBoolean("is_sotrx"),
                    windowType = WindowType.valueOf(rs.getString("window_type"))
                )
            },
            id
        )
        window.right()
    } catch (e: Exception) {
        logger.error("Failed to find window: $id", e)
        WindowError.NotFound(id).left()
    }
 
    private fun findTabsByWindowId(windowId: UUID): List<ADTab> {
        return jdbcTemplate.query(
            """
            SELECT t.*, 
                u1.username as created_by,
                u2.username as updated_by,
                t.version
            FROM ad_tab t
            LEFT JOIN ad_user u1 ON t.created_by = u1.id
            LEFT JOIN ad_user u2 ON t.updated_by = u2.id
            WHERE t.window_id = ? 
            ORDER BY t.sequence
            """,
            { rs, _ ->
                ADTab(
                    metadata = EntityMetadata(
                        id = rs.getString("id"),
                        audit = AuditInfo(
                            createdBy = rs.getString("created_by"),
                            updatedBy = rs.getString("updated_by")
                        ),
                        version = VersionInfo(version = rs.getInt("version"))
                    ),
                    displayName = DisplayName.create(rs.getString("display_name")).orNull()!!,
                    name = TabName.create(rs.getString("name")).orNull()!!,
                    description = rs.getString("description")?.let { 
                        Description.create(it).orNull() 
                    },
                    tableId = UUID.fromString(rs.getString("table_id")),
                    sequence = rs.getInt("sequence"),
                    fields = findFieldsByTabId(UUID.fromString(rs.getString("id")))
                )
            },
            windowId
        )
    }

    override suspend fun findByName(name: WindowName): Either<WindowError, ADWindow?> = try {
        val window = jdbcTemplate.queryForObject(
            """
            SELECT w.*, 
                u1.username as created_by,
                u2.username as updated_by,
                w.version
            FROM ad_window w
            LEFT JOIN ad_user u1 ON w.created_by = u1.id
            LEFT JOIN ad_user u2 ON w.updated_by = u2.id
            WHERE w.name = ?
            """,
            { rs, _ ->
                ADWindow(
                    metadata = EntityMetadata(
                        id = rs.getString("id"),
                        audit = AuditInfo(
                            createdBy = rs.getString("created_by"),
                            updatedBy = rs.getString("updated_by")
                        ),
                        version = VersionInfo(version = rs.getInt("version"))
                    ),
                    displayName = DisplayName.create(rs.getString("display_name")).orNull()!!,
                    name = WindowName.create(rs.getString("name")).orNull()!!,
                    description = rs.getString("description")?.let { 
                        Description.create(it).orNull() 
                    },
                    tabs = findTabsByWindowId(UUID.fromString(rs.getString("id"))),
                    isActive = rs.getBoolean("is_active"),
                    isSOTrx = rs.getBoolean("is_sotrx"),
                    windowType = WindowType.valueOf(rs.getString("window_type"))
                )
            },
            name.value
        )
        window.right()
    } catch (e: Exception) {
        logger.error("Failed to find window by name: ${name.value}", e)
        null.right()
    }
 
    override suspend fun search(query: String, pageSize: Int, page: Int): Flow<ADWindow> = flow {
        val sql = """
            SELECT w.*,
                u1.username as created_by,
                u2.username as updated_by,
                w.version
            FROM ad_window w
            LEFT JOIN ad_user u1 ON w.created_by = u1.id
            LEFT JOIN ad_user u2 ON w.updated_by = u2.id
            WHERE LOWER(w.name) LIKE LOWER(?) 
            OR LOWER(w.display_name) LIKE LOWER(?)
            ORDER BY w.name
            LIMIT ? OFFSET ?
        """
        
        val searchPattern = "%$query%"
        val offset = page * pageSize
        
        try {
            jdbcTemplate.query(
                sql,
                { rs, _ ->
                    ADWindow(
                        metadata = EntityMetadata(
                            id = rs.getString("id"),
                            audit = AuditInfo(
                                createdBy = rs.getString("created_by"),
                                updatedBy = rs.getString("updated_by")
                            ),
                            version = VersionInfo(version = rs.getInt("version"))
                        ),
                        displayName = DisplayName.create(rs.getString("display_name")).orNull()!!,
                        name = WindowName.create(rs.getString("name")).orNull()!!,
                        description = rs.getString("description")?.let { 
                            Description.create(it).orNull() 
                        },
                        tabs = findTabsByWindowId(UUID.fromString(rs.getString("id"))),
                        isActive = rs.getBoolean("is_active"),
                        isSOTrx = rs.getBoolean("is_sotrx"),
                        windowType = WindowType.valueOf(rs.getString("window_type"))
                    )
                },
                searchPattern,
                searchPattern,
                pageSize,
                offset
            ).forEach { window ->
                emit(window)
            }
        } catch (e: Exception) {
            logger.error("Search failed for query: $query", e)
            // Let the flow complete empty rather than error
        }
    }
}
