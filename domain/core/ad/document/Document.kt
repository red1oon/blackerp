package org.blackerp.domain.ad.document

import org.blackerp.domain.EntityMetadata
import org.blackerp.domain.ad.ADObject
import org.blackerp.domain.values.DisplayName
import org.blackerp.domain.values.Description
import arrow.core.Either
import arrow.core.right
import java.util.UUID

data class Document(
    override val metadata: EntityMetadata,
    val id: UUID,
    override val displayName: DisplayName,
    override val description: Description?,
    val type: DocumentType,
    val basedOn: UUID?,
    val status: DocumentStatus,
    val lines: List<DocumentLine>
) : ADObject {
    companion object {
        fun create(params: CreateDocumentParams): Either<DocumentError, Document> =
            Document(
                metadata = params.metadata,
                id = params.id,
                displayName = params.displayName,
                description = params.description,
                type = params.type,
                basedOn = params.basedOn,
                status = DocumentStatus.DRAFT,
                lines = params.lines
            ).right()
    }
}
