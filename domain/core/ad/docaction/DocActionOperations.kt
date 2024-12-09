package org.blackerp.domain.core.ad.docaction

import arrow.core.Either
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface DocActionOperations {
    suspend fun register(action: DocAction): Either<DocActionError, DocAction>
    suspend fun findByCode(code: String): Either<DocActionError, DocAction?>
    suspend fun execute(
        code: String, 
        context: DocActionContext
    ): Either<DocActionError, DocActionResult>
    suspend fun listActions(): Flow<DocAction>
}
