package org.blackerp.domain.core.ad.base

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import org.blackerp.domain.core.EntityMetadata
import org.blackerp.domain.core.ad.value.ModuleName
import org.blackerp.domain.core.values.DisplayName
import org.blackerp.domain.core.values.Description
import org.blackerp.domain.core.shared.ValidationError
import org.blackerp.plugin.Version

data class ADModule(
    override val metadata: EntityMetadata,
    val name: ModuleName,
    override val displayName: DisplayName,
    override val description: Description?,
    val version: Version
) : ADObject {
    companion object {
        fun create(params: CreateModuleParams): Either<ValidationError, ADModule> =
            ADModule(
                metadata = params.metadata,
                name = params.name,
                displayName = params.displayName,
                description = params.description,
                version = params.version
            ).right()
    }
}

data class CreateModuleParams(
    val metadata: EntityMetadata,
    val name: ModuleName,
    val displayName: DisplayName,
    val description: Description?,
    val version: Version
)
