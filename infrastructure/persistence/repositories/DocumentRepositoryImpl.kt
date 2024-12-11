// File: infrastructure/persistence/repositories/DocumentRepositoryImpl.kt
package org.blackerp.infrastructure.persistence.repositories

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import java.sql.ResultSet
import java.util.UUID
import kotlinx.coroutines.flow.*
import org.blackerp.domain.core.ad.document.*
import org.blackerp.domain.core.shared.AuditInfo
import org.blackerp.domain.core.shared.EntityMetadata
import org.blackerp.domain.core.values.*
import org.blackerp.domain.events.DocumentEvent
import org.blackerp.domain.events.EventMetadata
import org.blackerp.infrastructure.events.publishers.DomainEventPublisher
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class DocumentRepositoryImpl(
    private val jdbcTemplate: JdbcTemplate,
    private val eventPublisher: DomainEventPublisher
) : DocumentOperations {

    private val logger = LoggerFactory.getLogger(DocumentRepositoryImpl::class.java)

    @Transactional
    override suspend fun create(document: Document): Either<DocumentError, Document> {
        logger.debug("Creating document: ${document.id}")
        return try {
            jdbcTemplate.update("""
                INSERT INTO ad_document (
                    id, type_id, display_name, description, status,
                    created_by, created_at, updated_by, updated_at
                ) VALUES (?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, ?, CURRENT_TIMESTAMP)
            """,
                document.id,
                document.type.id,
                document.displayName.value,
                document.description?.value,
                document.status.name,
                document.metadata.audit.createdBy,
                document.metadata.audit.updatedBy
            )

            document.attributes.forEach { (key, value) ->
                jdbcTemplate.update("""
                    INSERT INTO ad_document_attribute (document_id, name, value, created_by)
                    VALUES (?, ?, ?, ?)
                """,
                    document.id,
                    key,
                    value.toString(),
                    document.metadata.audit.createdBy
                )
            }

            publishDocumentCreated(document)
            document.right()
        } catch (e: Exception) {
            logger.error("Failed to create document: ${document.id}", e)
            DocumentError.ValidationFailed("Failed to create document: ${e.message}").left()
        }
    }

    @Transactional
    override suspend fun update(id: UUID, document: Document): Either<DocumentError, Document> {
        logger.debug("Updating document: $id")
        return try {
            val updated = jdbcTemplate.update("""
                UPDATE ad_document 
                SET display_name = ?, description = ?, updated_by = ?, updated_at = CURRENT_TIMESTAMP
                WHERE id = ?
            """,
                document.displayName.value,
                document.description?.value,
                document.metadata.audit.updatedBy,
                id
            )

            if (updated == 0) {
                DocumentError.NotFound(id).left()
            } else {
                document.right()
            }
        } catch (e: Exception) {
            logger.error("Failed to update document: $id", e)
            DocumentError.ValidationFailed("Update failed: ${e.message}").left()
        }
    }

    @Transactional(readOnly = true)
    override suspend fun findById(id: UUID): Either<DocumentError, Document?> {
        return try {
            val document = jdbcTemplate.query(
                "SELECT * FROM ad_document WHERE id = ?",
                { rs, _ ->
                    Document.create(
                        metadata = EntityMetadata(
                            id = rs.getString("id"),
                            audit = AuditInfo(
                                createdBy = rs.getString("created_by"),
                                updatedBy = rs.getString("updated_by")
                            )
                        ),
                        displayName = DisplayName.create(rs.getString("display_name")).orNull()!!,
                        description = rs.getString("description")?.let { Description.create(it).orNull() },
                        type = loadDocumentType(UUID.fromString(rs.getString("type_id"))),
                        status = DocumentStatus.valueOf(rs.getString("status"))
                    )
                },
                id
            ).firstOrNull()

            document.right()
        } catch (e: Exception) {
            logger.error("Error finding document: $id", e)
            DocumentError.NotFound(id).left()
        }
    }

    override suspend fun search(criteria: SearchCriteria): Flow<Document> = flow {
        val sql = buildSearchQuery(criteria)
        try {
            jdbcTemplate.query(sql) { rs, _ ->
                Document.create(
                    metadata = EntityMetadata(
                        id = rs.getString("id"),
                        audit = AuditInfo(
                            createdBy = rs.getString("created_by"),
                            updatedBy = rs.getString("updated_by")
                        )
                    ),
                    displayName = DisplayName.create(rs.getString("display_name")).orNull()!!,
                    description = rs.getString("description")?.let { Description.create(it).orNull() },
                    type = loadDocumentType(UUID.fromString(rs.getString("type_id"))),
                    status = DocumentStatus.valueOf(rs.getString("status"))
                )
            }.forEach { emit(it) }
        } catch (e: Exception) {
            logger.error("Search failed", e)
        }
    }

    @Transactional
    override suspend fun delete(id: UUID): Either<DocumentError, Unit> {
        return try {
            val deleted = jdbcTemplate.update("DELETE FROM ad_document WHERE id = ?", id)
            if (deleted > 0) Unit.right() else DocumentError.NotFound(id).left()
        } catch (e: Exception) {
            logger.error("Failed to delete document: $id", e)
            DocumentError.ValidationFailed("Delete failed: ${e.message}").left()
        }
    }

    @Transactional
    override suspend fun changeStatus(id: UUID, status: DocumentStatus): Either<DocumentError, Document> {
        return try {
            val updated = jdbcTemplate.update("""
            UPDATE ad_document 
            SET status = ?, updated_at = CURRENT_TIMESTAMP
            WHERE id = ?
        """, status.name, id)

            if (updated == 0) {
                DocumentError.NotFound(id).left()
            } else {
                // Get updated document
                findById(id).fold(
                    { error -> error.left() },
                    { document -> document?.right() ?: DocumentError.NotFound(id).left() }
                )
            }
        } catch (e: Exception) {
            logger.error("Failed to change status for document: $id", e)
            DocumentError.ValidationFailed("Status change failed: ${e.message}").left()
        }
    }

    override suspend fun getHistory(id: UUID): Flow<DocumentChange> = flow {
        try {
            jdbcTemplate.query("""
                SELECT * FROM ad_document_history 
                WHERE document_id = ? 
                ORDER BY changed_at DESC
            """,
                { rs, _ ->
                    DocumentChange(
                        id = UUID.fromString(rs.getString("id")),
                        documentId = UUID.fromString(rs.getString("document_id")),
                        changedAt = rs.getTimestamp("changed_at").toInstant(),
                        changedBy = rs.getString("changed_by"),
                        changes = mapOf() // TODO: Implement changes mapping
                    )
                },
                id).forEach { emit(it) }
        } catch (e: Exception) {
            logger.error("Failed to get history for document: $id", e)
        }
    }

    private fun buildSearchQuery(criteria: SearchCriteria): String {
        val sql = StringBuilder("SELECT * FROM ad_document WHERE 1=1")

        criteria.types?.let { types ->
            sql.append(" AND type_id IN (${types.joinToString(",") { "'$it'" }})")
        }

        criteria.statuses?.let { statuses ->
            sql.append(" AND status IN (${statuses.joinToString(",") { "'${it.name}'" }})")
        }

        criteria.dateRange?.let { range ->
            sql.append(" AND created_at BETWEEN '${range.from}' AND '${range.to}'")
        }

        sql.append(" ORDER BY created_at DESC")
        sql.append(" LIMIT ${criteria.pageSize} OFFSET ${criteria.page * criteria.pageSize}")

        return sql.toString()
    }

    private fun loadDocumentType(typeId: UUID): DocumentType {
        return jdbcTemplate.query(
            """
            SELECT dt.*, sc.* 
            FROM ad_document_type dt
            LEFT JOIN ad_doc_status_config sc ON dt.id = sc.type_id
            WHERE dt.id = ?
            """,
            { rs: ResultSet, _: Int ->
                DocumentType(
                    metadata = EntityMetadata(
                        id = rs.getString("id"),
                        audit = AuditInfo(
                            createdBy = rs.getString("created_by"),
                            updatedBy = rs.getString("updated_by")
                        )
                    ),
                    displayName = DisplayName.create(rs.getString("display_name")).orNull()!!,
                    description = rs.getString("description")?.let { Description.create(it).orNull() },
                    name = rs.getString("name"),
                    baseTableId = UUID.fromString(rs.getString("base_table_id")),
                    linesTableId = rs.getString("lines_table_id")?.let { UUID.fromString(it) },
                    workflowId = rs.getString("workflow_id")?.let { UUID.fromString(it) },
                    statusConfig = DocumentStatusConfig(
                        statuses = loadStatusDefinitions(typeId),
                        defaultStatus = rs.getString("default_status"),
                        closingStatuses = loadClosingStatuses(typeId)
                    ),
                    isSOTrx = rs.getBoolean("is_sotrx"),
                    isActive = rs.getBoolean("is_active")
                )
            },
            typeId
        ).firstOrNull() ?: throw IllegalStateException("Document type not found: $typeId")
    }

    private fun loadStatusDefinitions(typeId: UUID): Map<String, DocumentStatusDef> {
        return jdbcTemplate.query(
            """
            SELECT * FROM ad_doc_status 
            WHERE type_id = ?
            """,
            { rs, _ ->
                val code = rs.getString("code")
                code to DocumentStatusDef(
                    code = code,
                    name = rs.getString("name"),
                    description = rs.getString("description"),
                    nextStatuses = loadNextStatuses(typeId, code)
                )
            },
            typeId
        ).toMap()
    }

    private fun loadNextStatuses(typeId: UUID, fromStatus: String): Set<String> {
        return jdbcTemplate.query(
            """
            SELECT to_status FROM ad_doc_status_transition 
            WHERE type_id = ? AND from_status = ?
            """,
            { rs, _ -> rs.getString("to_status") },
            typeId,
            fromStatus
        ).toSet()
    }

    private fun loadClosingStatuses(typeId: UUID): Set<String> {
        return jdbcTemplate.query(
            """
            SELECT code FROM ad_doc_status 
            WHERE type_id = ? AND is_closing = true
            """,
            { rs, _ -> rs.getString("code") },
            typeId
        ).toSet()
    }

    private fun publishDocumentCreated(document: Document) {
        eventPublisher.publish(
            DocumentEvent.DocumentCreated(
                metadata = EventMetadata(
                    user = document.metadata.audit.createdBy,
                    correlationId = UUID.randomUUID().toString()
                ),
                documentId = UUID.fromString(document.id),
                type = document.type.name,
                status = document.status
            )
        )
    }
}