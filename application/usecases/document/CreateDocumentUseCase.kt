package org.blackerp.application.usecases.document

import org.springframework.stereotype.Service
import org.blackerp.domain.core.ad.document.*
import org.blackerp.domain.core.metadata.*
import org.blackerp.domain.core.values.*
import org.blackerp.application.services.base.CoroutineBaseService
import arrow.core.*
import java.util.UUID
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers

@Service
class CreateDocumentUseCase(
    private val documentOperations: DocumentOperations
) : CoroutineBaseService() {
    suspend fun execute(command: CreateDocumentCommand): Either<DocumentError, Document> =
        withContext(Dispatchers.IO) {
            val description = command.description?.let { desc ->
                Description.create(desc).fold(
                    { DocumentError.ValidationFailed("Invalid description").left() },
                    { it.right() }
                )
            } ?: DocumentError.ValidationFailed("Description required").left()

            description.flatMap { desc ->
                DisplayName.create(command.displayName).fold(
                    { DocumentError.ValidationFailed("Invalid display name").left() },
                    { displayName ->
                        val document = Document(
                            metadata = EntityMetadata(
                                id = UUID.randomUUID().toString(),
                                audit = AuditInfo(createdBy = "system", updatedBy = "system")
                            ),
                            displayName = displayName,
                            description = desc,
                            type = command.documentType
                        )
                        documentOperations.create(document)
                    }
                )
            }
        }
}
