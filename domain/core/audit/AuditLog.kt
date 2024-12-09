package org.blackerp.domain.core.audit

import java.time.Instant
import java.util.UUID

data class AuditLog(
    val id: UUID = UUID.randomUUID(),
    val timestamp: Instant = Instant.now(),
    val entityType: String,
    val entityId: UUID,
    val action: String,
    val userId: UUID,
    val username: String,
    val clientId: UUID,
    val organizationId: UUID?,
    val oldValue: String?,
    val newValue: String?,
    val metadata: Map<String, String> = emptyMap()
)

interface AuditLogger {
    suspend fun log(entry: AuditLog)
    suspend fun getAuditTrail(
        entityType: String,
        entityId: UUID,
        fromDate: Instant? = null,
        toDate: Instant? = null
    ): List<AuditLog>
}
