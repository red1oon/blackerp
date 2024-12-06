package org.blackerp.domain.core.ad.docaction

import arrow.core.Either
import org.blackerp.domain.core.metadata.EntityMetadata
import org.blackerp.domain.core.ad.base.ADObject
import org.blackerp.domain.core.values.DisplayName
import org.blackerp.domain.core.values.Description
import java.util.UUID

interface DocAction {
    val metadata: EntityMetadata
    val id: String
    val code: String
    val displayName: DisplayName
    val description: Description?
    
    suspend fun execute(context: DocActionContext): Either<DocActionError, DocActionResult>
}

data class DocActionContext(
    val documentId: UUID,
    val userId: String,
    val fromStatus: String,
    val toStatus: String,
    val parameters: Map<String, Any> = emptyMap()
)

data class DocActionResult(
    val success: Boolean,
    val message: String,
    val data: Map<String, Any> = emptyMap()
)
