package org.blackerp.domain.core.ad.process

import org.blackerp.domain.core.shared.EntityMetadata
import org.blackerp.domain.core.ad.base.ADObject
import org.blackerp.domain.core.values.DisplayName
import org.blackerp.domain.core.values.Description
import arrow.core.Either
import arrow.core.right
import java.util.UUID

data class ADProcess(
    override val metadata: EntityMetadata,
    override val id: String = UUID.randomUUID().toString(),
    override val displayName: DisplayName,
    override val description: Description?,
    val type: ProcessType,
    val parameters: List<ProcessParameter>,
    val implementation: ProcessImplementation,
    val schedule: ProcessSchedule?
) : ADObject {
    companion object {
        fun create(params: CreateProcessParams): Either<ProcessError, ADProcess> =
            ADProcess(
                metadata = params.metadata,
                displayName = params.displayName,
                description = params.description,
                type = params.type,
                parameters = params.parameters,
                implementation = params.implementation,
                schedule = params.schedule
            ).right()
    }
}

sealed interface ProcessImplementation {
    data class JavaClass(val className: String) : ProcessImplementation
    data class DatabaseFunction(val functionName: String) : ProcessImplementation
    data class Script(val language: String, val code: String) : ProcessImplementation
}

data class ProcessSchedule(
    val cronExpression: String,
    val enabled: Boolean = true
)
