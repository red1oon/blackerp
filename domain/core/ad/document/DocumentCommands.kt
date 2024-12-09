package org.blackerp.domain.core.ad.document

import org.blackerp.domain.core.values.DisplayName
import org.blackerp.domain.core.values.Description
import java.time.Instant

data class CreateDocumentCommand(
    val type: DocumentType,
    val displayName: DisplayName,
    val description: Description? = null,
    val attributes: Map<String, Any> = emptyMap()
)

data class UpdateDocumentCommand(
    val displayName: DisplayName? = null,
    val description: Description? = null,
    val attributes: Map<String, Any>? = null
)

data class ChangeStatusCommand(
    val targetStatus: DocumentStatus,
    val reason: String? = null,
    val attributes: Map<String, Any> = emptyMap()
)

data class DocumentSearchCriteria(
    val types: Set<DocumentType>? = null,
    val statuses: Set<DocumentStatus>? = null,
    val fromDate: Instant? = null,
    val toDate: Instant? = null,
    val pageSize: Int = 20,
    val page: Int = 0
)
