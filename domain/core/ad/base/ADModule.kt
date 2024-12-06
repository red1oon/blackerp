package org.blackerp.domain.core.ad.base

import arrow.core.Either
import arrow.core.right
import org.blackerp.domain.core.metadata.EntityMetadata
import org.blackerp.domain.core.values.DisplayName
import org.blackerp.domain.core.values.Description
import org.blackerp.domain.core.Version
import java.util.UUID

data class ADModule(
    override val metadata: EntityMetadata,
    private val uuid: UUID = UUID.randomUUID(),
    val name: ModuleName,
    override val displayName: DisplayName,
    override val description: Description?,
    val version: Version
) : ADObject {
    override val id: String get() = uuid.toString()
}
