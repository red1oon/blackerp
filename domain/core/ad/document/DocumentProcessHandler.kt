package org.blackerp.domain.core.ad.document

import org.blackerp.domain.core.ad.process.*
import arrow.core.Either
import arrow.core.left
import arrow.core.right
import org.slf4j.LoggerFactory
import java.util.UUID

interface DocumentProcessHandler {
    suspend fun executeDocumentProcess(
        document: Document,
        processId: UUID,
        parameters: Map<String, Any>
    ): Either<DocumentError, ProcessResult>
}

class DocumentProcessHandlerImpl(
    private val processOperations: ProcessOperations
) : DocumentProcessHandler {
    private val logger = LoggerFactory.getLogger(DocumentProcessHandlerImpl::class.java)

    override suspend fun executeDocumentProcess(
        document: Document,
        processId: UUID,
        parameters: Map<String, Any>
    ): Either<DocumentError, ProcessResult> {
        logger.debug("Executing process $processId for document ${document.id}")

        val documentParameters = createDocumentParameters(document)
        val mergedParameters = parameters + documentParameters

        return processOperations.execute(processId, mergedParameters)
            .mapLeft { error ->
                DocumentError.ValidationFailed("Process execution failed: ${error.message}")
            }
    }

    private fun createDocumentParameters(document: Document): Map<String, Any> = mapOf(
        "DocumentId" to document.id,
        "DocumentType" to document.type.id,
        "DocumentStatus" to document.status.name
    )
}