// File: domain/core/ad/document/SearchCriteria.kt
package org.blackerp.domain.core.ad.document

import java.time.Instant
import java.util.UUID

data class SearchCriteria(
        val types: List<UUID>? = null,
        val statuses: List<DocumentStatus>? = null,
        val dateRange: DateRange? = null,
        val pageSize: Int = 20,
        val page: Int = 0
)

data class DateRange(val from: Instant, val to: Instant)
