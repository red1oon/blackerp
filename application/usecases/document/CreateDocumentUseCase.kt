package org.blackerp.application.usecases.document

import org.blackerp.domain.core.ad.document.*
import org.springframework.stereotype.Service
import arrow.core.Either

@Service
class CreateDocumentUseCase(
    private val documentOperations: DocumentOperations
) {
    suspend fun execute(command: CreateDocumentCommand): Either<DocumentError, Document> =
        command.toDocument().flatMap { document ->
            documentOperations.save(document)
        }
}

data class CreateDocumentCommand(
    val displayName: String,
    val description: String?,
    val typeId: UUID,
    val basedOn: UUID?,
    val lines: List<CreateDocumentLineCommand>
)

data class CreateDocumentLineCommand(
    val lineNo: Int,
    val attributes: Map<String, Any>
)
