package org.blackerp.application.services.docaction

import org.springframework.stereotype.Service
import org.blackerp.domain.core.ad.docaction.*
import org.blackerp.application.services.docstatus.DocStatusService
import arrow.core.Either
import arrow.core.right
import arrow.core.left
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.slf4j.LoggerFactory
import java.util.UUID

@Service
class DocActionService(
    private val docStatusService: DocStatusService
) : DocActionOperations {
    private val logger = LoggerFactory.getLogger(DocActionService::class.java)
    
    // In-memory storage for POC
    private val actions = mutableMapOf<String, DocAction>()
    
    override suspend fun register(action: DocAction): Either<DocActionError, DocAction> {
        logger.debug("Registering action: ${action.code}")
        actions[action.code] = action
        return action.right()
    }
    
    override suspend fun findByCode(code: String): Either<DocActionError, DocAction?> {
        val action = actions[code]
        return action?.right() ?: DocActionError.NotFound(code).left()
    }
    
    override suspend fun execute(code: String, context: DocActionContext): Either<DocActionError, DocActionResult> {
        val action = actions[code] ?: return DocActionError.NotFound(code).left()
        return action.execute(context)
    }
    
    override suspend fun listActions(): Flow<DocAction> = 
        flowOf(*actions.values.toTypedArray())
}
