package org.blackerp.infrastructure.audit

import org.springframework.stereotype.Component
import org.blackerp.domain.core.audit.AuditLog
import org.blackerp.domain.core.audit.AuditLogger
import java.time.Instant
import java.util.UUID
import org.slf4j.LoggerFactory

@Component
class AuditLoggerImpl : AuditLogger {
    private val logger = LoggerFactory.getLogger(AuditLoggerImpl::class.java)
    
    // In-memory storage for POC
    private val auditLogs = mutableListOf<AuditLog>()

    override suspend fun log(entry: AuditLog) {
        auditLogs.add(entry)
        logger.info("Audit log created: ${entry.action} on ${entry.entityType}:${entry.entityId}")
    }

    override suspend fun getAuditTrail(
        entityType: String,
        entityId: UUID,
        fromDate: Instant?,
        toDate: Instant?
    ): List<AuditLog> =
        auditLogs
            .filter { log ->
                log.entityType == entityType &&
                log.entityId == entityId &&
                (fromDate == null || log.timestamp >= fromDate) &&
                (toDate == null || log.timestamp <= toDate)
            }
            .sortedByDescending { it.timestamp }
}
