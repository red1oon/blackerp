package org.blackerp.domain.ad.process

import org.blackerp.domain.EntityMetadata
import org.blackerp.domain.ad.ADObject
import org.blackerp.domain.values.DisplayName
import org.blackerp.domain.values.Description
import arrow.core.Either
import arrow.core.right
import java.util.UUID

data class ADProcess(
    override val metadata: EntityMetadata,
    val id: UUID,
    override val displayName: DisplayName,
    override val description: Description?,
    val parameters: List<ADParameter>,
    val implementation: ProcessImplementation,
    val schedule: ProcessSchedule?
) : ADObject {
    companion object {
        fun create(params: CreateProcessParams): Either<ProcessError, ADProcess> =
            ADProcess(
                metadata = params.metadata,
                id = params.id,
                displayName = params.displayName,
                description = params.description,
                parameters = params.parameters,
                implementation = params.implementation,
                schedule = params.schedule
            ).right()
    }
}
