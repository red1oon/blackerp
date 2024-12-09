package org.blackerp.domain.core.ad.document

import java.time.Instant
import java.util.UUID
import org.blackerp.domain.core.shared.ChangePair

// Unified document models
data class DocumentChange(
    val id: UUID,
    val documentId: UUID,
    val changedAt: Instant,
    val changedBy: String,
    val changes: Map<String, ChangePair<*>>
)

data class DateRange(
    val from: Instant, 
    val to: Instant
)

data class SearchCriteria(
    val types: List<UUID>? = null,
    val statuses: List<DocumentStatus>? = null,
    val dateRange: DateRange? = null,
    val pageSize: Int = 20,
    val page: Int = 0
)
