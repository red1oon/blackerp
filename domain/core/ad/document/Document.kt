package org.blackerp.domain.core.ad.document

import arrow.core.Either
import arrow.core.right
import org.blackerp.domain.core.shared.EntityMetadata
import org.blackerp.domain.core.values.DisplayName
import org.blackerp.domain.core.values.Description
import org.blackerp.domain.core.ad.base.ADObject
import java.time.Instant
import java.util.UUID

data class Document(
    override val metadata: EntityMetadata,
    private val uuid: UUID = UUID.randomUUID(),
    override val displayName: DisplayName,
    override val description: Description?,
    val type: DocumentType,
    val status: DocumentStatus = DocumentStatus.DRAFT,
    val created: Instant = Instant.now(),
    val lastModified: Instant = Instant.now()
) : ADObject {
    override val id: String get() = uuid.toString()
}
