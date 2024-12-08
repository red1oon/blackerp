// File: domain/core/ad/document/Document.kt
package org.blackerp.domain.core.ad.document

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import java.util.UUID
import org.blackerp.domain.core.ad.base.ADObject
import org.blackerp.domain.core.shared.EntityMetadata
import org.blackerp.domain.core.values.Description
import org.blackerp.domain.core.values.DisplayName

data class Document(
        override val metadata: EntityMetadata,
        private val uuid: UUID = UUID.randomUUID(),
        override val displayName: DisplayName,
        override val description: Description?,
        val type: DocumentType,
        val status: DocumentStatus = DocumentStatus.DRAFT,
        val attributes: Map<String, Any> = emptyMap()
) : ADObject {
    override val id: String
        get() = uuid.toString()

    fun validateStatusTransition(targetStatus: DocumentStatus): Either<DocumentError, Unit> =
            if (type.validateStatusTransition(status.name, targetStatus.name)) {
                Unit.right()
            } else {
                DocumentError.StatusTransitionInvalid(
                                currentStatus = status,
                                targetStatus = targetStatus
                        )
                        .left()
            }

    companion object {
        fun create(
                metadata: EntityMetadata,
                displayName: DisplayName,
                description: Description?,
                type: DocumentType,
                status: DocumentStatus = DocumentStatus.DRAFT,
                attributes: Map<String, Any> = emptyMap()
        ): Document =
                Document(
                        metadata = metadata,
                        displayName = displayName,
                        description = description,
                        type = type,
                        status = status,
                        attributes = attributes
                )
    }
}
