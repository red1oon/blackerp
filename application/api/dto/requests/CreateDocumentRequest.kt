package org.blackerp.application.api.dto.requests

import java.util.UUID

data class CreateDocumentRequest(
    val displayName: String,
    val description: String?,
    val typeId: UUID,
    val attributes: Map<String, Any> = emptyMap()
)
