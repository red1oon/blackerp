package org.blackerp.domain.core.ad.docstatus

import arrow.core.Either
import arrow.core.right
import arrow.core.left
import org.blackerp.domain.core.shared.EntityMetadata
import org.blackerp.domain.core.ad.base.ADObject
import org.blackerp.domain.core.values.DisplayName
import org.blackerp.domain.core.values.Description
import java.util.UUID

enum class DocumentStatus {
    DRAFT,
    IN_PROGRESS, 
    COMPLETED,
    VOIDED,
    CLOSED
}

data class DocStatus(
    override val metadata: EntityMetadata,
    private val uuid: UUID = UUID.randomUUID(),
    override val displayName: DisplayName,
    override val description: Description?,
    val code: String,
    val allowedTransitions: List<String>
) : ADObject {
    override val id: String get() = uuid.toString()
    
    companion object {
        fun create(
            metadata: EntityMetadata,
            displayName: DisplayName,
            description: Description?,
            code: String,
            transitions: List<String>
        ): Either<DocStatusError, DocStatus> = 
            DocStatus(
                metadata = metadata,
                displayName = displayName,
                description = description,
                code = code,
                allowedTransitions = transitions
            ).right()
    }
}
