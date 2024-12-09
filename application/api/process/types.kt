package org.blackerp.application.api.process

import org.blackerp.domain.core.metadata.*
import org.blackerp.domain.core.values.*
import org.blackerp.domain.core.ad.process.*
import arrow.core.*
import java.util.UUID

data class ProcessRequest(
    val name: String,
    val displayName: String,
    val description: String?,
    val parameters: List<DomainProcessParameter>
) {
    fun toDomain(): Either<ProcessError, ADProcess> {
        val metadata = EntityMetadata(
            id = UUID.randomUUID().toString(),
            audit = AuditInfo(createdBy = "system", updatedBy = "system")
        )

        return DisplayName.create(displayName)
            .mapLeft { ProcessError.ValidationFailed(it.message) }
            .flatMap { dispName ->
                val desc = description?.let { 
                    Description.create(it).fold(
                        { ProcessError.ValidationFailed(it.message).left() },
                        { it.right() }
                    )
                } ?: null.right()

                desc.map { description ->
                    ADProcess(
                        metadata = metadata,
                        displayName = dispName,
                        description = description,
                        type = ProcessType.CUSTOM,
                        parameters = parameters.map { 
                            ProcessParameter(
                                id = UUID.randomUUID(),
                                name = it.name,
                                displayName = it.displayName,
                                description = it.description,
                                parameterType = ParameterType.STRING,
                                isMandatory = false,
                                validationRule = null
                            )
                        },
                        implementation = ProcessImplementation.JavaClass(name),
                        schedule = null
                    )
                }
            }
    }
}
