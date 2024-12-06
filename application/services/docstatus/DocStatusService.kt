package org.blackerp.application.services.docstatus

import org.springframework.stereotype.Service
import org.blackerp.domain.core.ad.docstatus.*
import arrow.core.Either
import arrow.core.right
import arrow.core.left
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.util.UUID

@Service
class DocStatusService {
    // In-memory storage for POC
    private val statuses = mutableMapOf<String, DocStatus>()
    
    suspend fun getStatus(code: String): Either<DocStatusError, DocStatus?> {
        val status = statuses[code]
        return status?.right() ?: DocStatusError.NotFound(UUID.randomUUID()).left()
    }
    
    suspend fun validateTransition(from: String, to: String): Either<DocStatusError, Unit> {
        return when (val status = statuses[from]) {
            null -> DocStatusError.NotFound(UUID.randomUUID()).left()
            else -> {
                if (status.allowedTransitions.contains(to)) Unit.right()
                else DocStatusError.StatusTransitionInvalid(from, to).left()
            }
        }
    }
}
