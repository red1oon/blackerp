package org.blackerp.domain.core.ad.docaction

import org.springframework.stereotype.Component
import arrow.core.Either
import arrow.core.right
import arrow.core.left
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap

/**
 * In-memory document action registry for POC
 * Manages document action definitions and execution
 */
@Component
class DocActionRegistry {
    private val logger = LoggerFactory.getLogger(DocActionRegistry::class.java)
    private val actions = ConcurrentHashMap<String, DocAction>()

    fun register(action: DocAction): Either<DocActionError, DocAction> {
        logger.debug("Registering action: ${action.code}")
        actions[action.code] = action
        return action.right()
    }

    fun unregister(code: String) {
        actions.remove(code)
        logger.debug("Unregistered action: $code")
    }

    suspend fun execute(code: String, context: DocActionContext): Either<DocActionError, DocActionResult> {
        logger.debug("Executing action: $code")
        return actions[code]?.execute(context) 
            ?: DocActionError.NotFound(code).left()
    }

    fun getAction(code: String): Either<DocActionError, DocAction> =
        actions[code]?.right() ?: DocActionError.NotFound(code).left()

    // Standard document actions
    object StandardActions {
        const val START = "START"
        const val COMPLETE = "COMPLETE"
        const val VOID = "VOID"
        const val CLOSE = "CLOSE"
        
        fun registerStandardActions(registry: DocActionRegistry) {
            registry.register(createStartAction())
            registry.register(createCompleteAction())
            registry.register(createVoidAction())
            registry.register(createCloseAction())
        }

        private fun createStartAction() = object : DocAction {
            override val code = START
            override val displayName = "Start"
            override val description = "Start processing the document"

            override suspend fun execute(context: DocActionContext): Either<DocActionError, DocActionResult> =
                DocActionResult(true, "Document processing started").right()
        }

        private fun createCompleteAction() = object : DocAction {
            override val code = COMPLETE
            override val displayName = "Complete"
            override val description = "Complete the document"

            override suspend fun execute(context: DocActionContext): Either<DocActionError, DocActionResult> =
                DocActionResult(true, "Document completed").right()
        }

        private fun createVoidAction() = object : DocAction {
            override val code = VOID
            override val displayName = "Void"
            override val description = "Void the document"

            override suspend fun execute(context: DocActionContext): Either<DocActionError, DocActionResult> =
                DocActionResult(true, "Document voided").right()
        }

        private fun createCloseAction() = object : DocAction {
            override val code = CLOSE
            override val displayName = "Close"
            override val description = "Close the document"

            override suspend fun execute(context: DocActionContext): Either<DocActionError, DocActionResult> =
                DocActionResult(true, "Document closed").right()
        }
    }
}
