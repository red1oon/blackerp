package org.blackerp.infrastructure.persistence.repositories

import org.springframework.stereotype.Repository
import org.springframework.jdbc.core.JdbcTemplate
import org.blackerp.domain.core.ad.document.*
import org.blackerp.domain.core.shared.ChangePair
import arrow.core.Either
import arrow.core.right
import arrow.core.left
import java.util.UUID
import java.time.Instant
import org.slf4j.LoggerFactory

@Repository
class DocumentHistoryRepository(
    private val jdbcTemplate: JdbcTemplate
) {
    private val logger = LoggerFactory.getLogger(DocumentHistoryRepository::class.java)

    // In-memory storage for POC
    private val historyRecords = mutableListOf<DocumentChange>()

    fun trackChange(change: DocumentChange) {
        historyRecords.add(change)
        logger.debug("Tracked change for document: ${change.documentId}")
    }

    fun getHistory(documentId: UUID): List<DocumentChange> =
        historyRecords
            .filter { it.documentId == documentId }
            .sortedByDescending { it.changedAt }

    fun getHistoryBetween(documentId: UUID, fromDate: Instant, toDate: Instant): List<DocumentChange> =
        historyRecords
            .filter { it.documentId == documentId && it.changedAt in fromDate..toDate }
            .sortedByDescending { it.changedAt }
}
