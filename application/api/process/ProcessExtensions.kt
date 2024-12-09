package org.blackerp.application.api.process

import org.blackerp.domain.core.ad.process.*
import org.blackerp.domain.core.metadata.*
import org.blackerp.domain.core.values.*
import arrow.core.*
import java.util.UUID

fun CreateProcessCommand.toDomain(): Either<ProcessError, ADProcess> {
    return DisplayName.create(displayName)
        .mapLeft { ProcessError.ValidationFailed(it.message) }
        .flatMap { displayName ->
            val descriptionValue = description?.let { desc ->
                Description.create(desc).fold(
                    { ProcessError.ValidationFailed(it.message).left() },
                    { it.right() }
                )
            } ?: null.right()
            
            descriptionValue.map { desc ->
                val metadata = EntityMetadata(
                    id = UUID.randomUUID().toString(),
                    audit = AuditInfo(createdBy = "system", updatedBy = "system")
                )
                
                ADProcess(
                    metadata = metadata,
                    displayName = displayName,
                    description = desc,
                    type = type,
                    parameters = parameters.map { param ->
                        ProcessParameter(
                            id = UUID.randomUUID(),
                            name = param.name,
                            displayName = param.displayName,
                            description = param.description,
                            parameterType = ParameterType.STRING,
                            isMandatory = false,
                            validationRule = null
                        )
                    },
                    implementation = implementation,
                    schedule = schedule
                )
            }
        }
}
