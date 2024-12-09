package org.blackerp.application.services.audit

import org.springframework.stereotype.Service
import org.blackerp.domain.core.audit.AuditLog
import org.blackerp.domain.core.audit.AuditLogger
import org.blackerp.domain.core.security.SecurityContext
import java.time.Instant
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Service
class AuditService(
    private val auditLogger: AuditLogger
) {
    suspend fun logAction(
        context: SecurityContext,
        entityType: String,
        entityId: UUID,
        action: String,
        oldValue: String? = null,
        newValue: String? = null,
        metadata: Map<String, String> = emptyMap()
    ) = withContext(Dispatchers.IO) {
        val auditLog = AuditLog(
            entityType = entityType,
            entityId = entityId,
            action = action,
            userId = context.user.id,
            username = context.user.username,
            clientId = context.clientId,
            organizationId = context.organizationId,
            oldValue = oldValue,
            newValue = newValue,
            metadata = metadata
        )
        auditLogger.log(auditLog)
    }

    suspend fun getAuditTrail(
        entityType: String,
        entityId: UUID,
        fromDate: Instant? = null,
        toDate: Instant? = null
    ): List<AuditLog> = withContext(Dispatchers.IO) {
        auditLogger.getAuditTrail(entityType, entityId, fromDate, toDate)
    }
}
