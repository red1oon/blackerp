package org.blackerp.infrastructure.persistence.repositories

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import java.sql.ResultSet
import java.util.UUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.blackerp.domain.core.ad.document.*
import org.blackerp.domain.core.shared.AuditInfo
import org.blackerp.domain.core.shared.EntityMetadata
import org.blackerp.domain.core.values.*
import org.blackerp.domain.events.DocumentEvent
import org.blackerp.domain.events.EventMetadata
import org.blackerp.infrastructure.events.publishers.DomainEventPublisher
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class DocumentRepositoryImpl(
        private val jdbcTemplate: JdbcTemplate,
        private val eventPublisher: DomainEventPublisher
) : DocumentOperations {

        private val logger = LoggerFactory.getLogger(DocumentRepositoryImpl::class.java)

        private val documentMapper =
                RowMapper<Document> { rs: ResultSet, _: Int ->
                        Document.create(
                                metadata =
                                        EntityMetadata(
                                                id = rs.getString("id"),
                                                audit =
                                                        AuditInfo(
                                                                createdBy =
                                                                        rs.getString("created_by"),
                                                                updatedBy =
                                                                        rs.getString("updated_by")
                                                        )
                                        ),
                                displayName =
                                        DisplayName.create(rs.getString("display_name")).orNull()!!,
                                description =
                                        rs.getString("description")?.let {
                                                Description.create(it).orNull()
                                        },
                                type = loadDocumentType(UUID.fromString(rs.getString("type_id"))),
                                status = DocumentStatus.valueOf(rs.getString("status"))
                        )
                }

        @Transactional
        override suspend fun create(document: Document): Either<DocumentError, Document> {
                logger.debug("Creating document: ${document.id}")
                try {
                        jdbcTemplate.update(
                                """
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

                        saveAttributes(UUID.fromString(document.id), document.attributes)
                        publishDocumentCreated(document)
                        return document.right()
                } catch (e: Exception) {
                        logger.error("Failed to create document: ${document.id}", e)
                        return DocumentError.ValidationFailed(
                                        "Failed to create document: ${e.message}"
                                )
                                .left()
                }
        }

        private fun saveAttributes(documentId: UUID, attributes: Map<String, Any>) {
                attributes.forEach { (key, value) ->
                        jdbcTemplate.update(
                                """
               INSERT INTO ad_document_attribute (
                   document_id, name, value, created_by, created_at
               ) VALUES (?, ?, ?, 'system', CURRENT_TIMESTAMP)
               ON CONFLICT (document_id, name) DO UPDATE 
               SET value = EXCLUDED.value
           """,
                                documentId,
                                key,
                                value.toString()
                        )
                }
        }

        override suspend fun update(id: UUID, document: Document): Either<DocumentError, Document> {
                logger.debug("Updating document: $id")
                try {
                        val updated =
                                jdbcTemplate.update(
                                        """
               UPDATE ad_document 
               SET display_name = ?,
                   description = ?,
                   status = ?,
                   updated_by = ?,
                   updated_at = CURRENT_TIMESTAMP
               WHERE id = ?
           """,
                                        document.displayName.value,
                                        document.description?.value,
                                        document.status.name,
                                        document.metadata.audit.updatedBy,
                                        id
                                )

                        if (updated == 0) {
                                return DocumentError.NotFound(id).left()
                        }

                        jdbcTemplate.update(
                                "DELETE FROM ad_document_attribute WHERE document_id = ?",
                                id
                        )
                        saveAttributes(id, document.attributes)
                        publishDocumentUpdated(document)
                        return document.right()
                } catch (e: Exception) {
                        logger.error("Failed to update document: $id", e)
                        return DocumentError.ValidationFailed(
                                        "Failed to update document: ${e.message}"
                                )
                                .left()
                }
        }

        override suspend fun findById(id: UUID): Either<DocumentError, Document?> {
                try {
                        return jdbcTemplate
                                .query(
                                        """
                   SELECT d.*, dt.name as type_name, dt.display_name as type_display_name
                   FROM ad_document d
                   JOIN ad_document_type dt ON d.type_id = dt.id
                   WHERE d.id = ?
               """,
                                        documentMapper,
                                        id
                                )
                                .firstOrNull()
                                .right()
                } catch (e: Exception) {
                        logger.error("Failed to find document: $id", e)
                        return DocumentError.ValidationFailed(
                                        "Failed to find document: ${e.message}"
                                )
                                .left()
                }
        }

        override suspend fun search(criteria: SearchCriteria): Flow<Document> = flow {
                val conditions = mutableListOf<String>()
                val params = mutableListOf<Any>()

                criteria.types?.let { typesList ->
                        conditions.add(
                                "d.type_id IN ${typesList.joinToString(",", "(", ")") { "?" }}"
                        )
                        params.addAll(typesList)
                }

                criteria.statuses?.let { statusList ->
                        conditions.add(
                                "d.status IN ${statusList.joinToString(",", "(", ")") { "?" }}"
                        )
                        params.addAll(statusList.map { it.name })
                }

                criteria.dateRange?.let { dateRange ->
                        conditions.add("d.created_at BETWEEN ? AND ?")
                        params.add(dateRange.from)
                        params.add(dateRange.to)
                }

                val whereClause =
                        if (conditions.isNotEmpty()) {
                                "WHERE ${conditions.joinToString(" AND ")}"
                        } else ""

                val sql =
                        """
           SELECT d.*, dt.name as type_name, dt.display_name as type_display_name
           FROM ad_document d
           JOIN ad_document_type dt ON d.type_id = dt.id
           $whereClause
           ORDER BY d.created_at DESC
           LIMIT ? OFFSET ?
       """

                params.add(criteria.pageSize)
                params.add(criteria.pageSize * criteria.page)

                jdbcTemplate.query(sql, documentMapper, *params.toTypedArray()).forEach { emit(it) }
        }

        @Transactional
        override suspend fun delete(id: UUID): Either<DocumentError, Unit> {
                try {
                        jdbcTemplate.update(
                                "DELETE FROM ad_document_attribute WHERE document_id = ?",
                                id
                        )
                        val deleted =
                                jdbcTemplate.update("DELETE FROM ad_document WHERE id = ?", id)

                        return if (deleted > 0) Unit.right() else DocumentError.NotFound(id).left()
                } catch (e: Exception) {
                        logger.error("Failed to delete document: $id", e)
                        return DocumentError.ValidationFailed(
                                        "Failed to delete document: ${e.message}"
                                )
                                .left()
                }
        }

        @Transactional
        override suspend fun changeStatus(
                id: UUID,
                status: DocumentStatus
        ): Either<DocumentError, Document> {
                try {
                        return findById(id)
                                .fold(
                                        { error -> error.left() },
                                        { document ->
                                                document?.let { doc ->
                                                        doc.validateStatusTransition(status).map {
                                                                jdbcTemplate.update(
                                                                        """
                                   UPDATE ad_document
                                   SET status = ?,
                                       updated_by = ?,
                                       updated_at = CURRENT_TIMESTAMP
                                   WHERE id = ?
                               """,
                                                                        status.name,
                                                                        doc.metadata
                                                                                .audit
                                                                                .updatedBy,
                                                                        id
                                                                )
                                                                doc.copy(status = status)
                                                        }
                                                }
                                                        ?: DocumentError.NotFound(id).left()
                                        }
                                )
                } catch (e: Exception) {
                        logger.error("Failed to change status: $id", e)
                        return DocumentError.ValidationFailed(
                                        "Failed to change status: ${e.message}"
                                )
                                .left()
                }
        }

        override suspend fun getHistory(id: UUID): Flow<DocumentChange> = flow {
                try {
                        val sql =
                                """
               SELECT * FROM ad_document_history 
               WHERE document_id = ?
               ORDER BY changed_at DESC
           """

                        jdbcTemplate.query(
                                        sql,
                                        { rs, _ ->
                                                DocumentChange(
                                                        id = UUID.fromString(rs.getString("id")),
                                                        documentId =
                                                                UUID.fromString(
                                                                        rs.getString("document_id")
                                                                ),
                                                        changedAt =
                                                                rs.getTimestamp("changed_at")
                                                                        .toInstant(),
                                                        changedBy = rs.getString("changed_by"),
                                                        changes =
                                                                mapOf() // TODO: Deserialize changes
                                                        // from JSON
                                                        )
                                        },
                                        id
                                )
                                .forEach { emit(it) }
                } catch (e: Exception) {
                        logger.error("Failed to get document history: $id", e)
                }
        }

        private fun publishDocumentCreated(document: Document) {
                eventPublisher.publish(
                        DocumentEvent.DocumentCreated(
                                metadata =
                                        EventMetadata(
                                                user = document.metadata.audit.createdBy,
                                                correlationId = UUID.randomUUID().toString()
                                        ),
                                documentId = UUID.fromString(document.metadata.id),
                                type = document.type.name,
                                status = document.status
                        )
                )
        }

        private fun publishDocumentUpdated(document: Document) {
                eventPublisher.publish(
                        DocumentEvent.DocumentModified(
                                metadata =
                                        EventMetadata(
                                                user = document.metadata.audit.updatedBy,
                                                correlationId = UUID.randomUUID().toString()
                                        ),
                                documentId = UUID.fromString(document.metadata.id),
                                changes = mapOf() // TODO: Track actual changes
                        )
                )
        }

        private fun loadDocumentType(typeId: UUID): DocumentType {
                return jdbcTemplate.queryForObject(
                        """
           SELECT * FROM ad_document_type 
           WHERE id = ?
           """.trimIndent(),
                        arrayOf(typeId),
                        { rs: ResultSet, _: Int ->
                                DocumentType(
                                        metadata =
                                                EntityMetadata(
                                                        id = rs.getString("id"),
                                                        audit =
                                                                AuditInfo(
                                                                        createdBy =
                                                                                rs.getString(
                                                                                        "created_by"
                                                                                ),
                                                                        updatedBy =
                                                                                rs.getString(
                                                                                        "updated_by"
                                                                                )
                                                                )
                                                ),
                                        displayName =
                                                DisplayName.create(rs.getString("display_name"))
                                                        .orNull()!!,
                                        description =
                                                rs.getString("description")?.let {
                                                        Description.create(it).orNull()
                                                },
                                        name = rs.getString("name"),
                                        baseTableId =
                                                UUID.fromString(rs.getString("base_table_id")),
                                        linesTableId =
                                                rs.getString("lines_table_id")?.let {
                                                        UUID.fromString(it)
                                                },
                                        workflowId =
                                                rs.getString("workflow_id")?.let {
                                                        UUID.fromString(it)
                                                },
                                        statusConfig = loadStatusConfig(typeId),
                                        isSOTrx = rs.getBoolean("is_sotrx"),
                                        isActive = rs.getBoolean("is_active")
                                )
                        }
                )
                        ?: throw IllegalStateException("Document type not found: $typeId")
        }

        private fun loadStatusConfig(typeId: UUID): DocumentStatusConfig {
                val statuses =
                        jdbcTemplate.query(
                                        """
           SELECT * FROM ad_doc_status
           WHERE type_id = ?
           """,
                                        { rs: ResultSet, _: Int ->
                                                DocumentStatusDef(
                                                        code = rs.getString("code"),
                                                        name = rs.getString("name"),
                                                        description = rs.getString("description"),
                                                        nextStatuses =
                                                                loadNextStatuses(
                                                                        rs.getString("code")
                                                                )
                                                )
                                        },
                                        typeId
                                )
                                .associateBy { it.code }

                return DocumentStatusConfig(
                        statuses = statuses,
                        defaultStatus =
                                jdbcTemplate.queryForObject(
                                        "SELECT default_status FROM ad_document_type WHERE id = ?",
                                        String::class.java,
                                        typeId
                                )!!,
                        closingStatuses = loadClosingStatuses(typeId)
                )
        }

        private fun loadNextStatuses(statusCode: String): Set<String> {
                val sql = "SELECT to_status FROM ad_doc_status_transition WHERE from_status = ?"
                return jdbcTemplate
                        .query(sql, arrayOf(statusCode)) { rs, _ -> rs.getString("to_status") }
                        .toSet()
        }

        private fun loadClosingStatuses(typeId: UUID): Set<String> {
                val sql = "SELECT code FROM ad_doc_status WHERE type_id = ? AND is_closing = true"
                return jdbcTemplate
                        .query(sql, arrayOf(typeId)) { rs, _ -> rs.getString("code") }
                        .toSet()
        }
}
