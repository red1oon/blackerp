package org.blackerp.domain.core.ad.docaction

import arrow.core.Either
import arrow.core.right
import arrow.core.left
import org.blackerp.domain.core.shared.EntityMetadata
import org.blackerp.domain.core.values.DisplayName
import org.blackerp.domain.core.values.Description
import java.util.UUID

interface DocActionRegistry {
    fun register(action: DocAction): Either<DocActionError, DocAction>
    fun unregister(code: String)
    suspend fun execute(code: String, context: DocActionContext): Either<DocActionError, DocActionResult>
    fun getAction(code: String): Either<DocActionError, DocAction>
}

data class StandardDocAction(
    override val metadata: EntityMetadata,
    override val id: String,
    override val code: String,
    override val displayName: DisplayName,
    override val description: Description?,
    val operation: suspend (DocActionContext) -> Either<DocActionError, DocActionResult>
) : DocAction {
    override suspend fun execute(context: DocActionContext): Either<DocActionError, DocActionResult> = 
        operation(context)
}
