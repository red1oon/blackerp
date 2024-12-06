package org.blackerp.application.api.extensions

import org.blackerp.domain.core.ad.process.*
import org.blackerp.domain.core.metadata.*
import org.blackerp.domain.core.values.*
import org.blackerp.application.api.process.CreateProcessCommand
import arrow.core.*
import java.util.UUID

fun CreateProcessCommand.toDomain(): Either<ProcessError, ADProcess> =
   DisplayName.create(displayName).bimap(
       { ProcessError.ValidationFailed(it.message) },
       { displayName -> 
           ADProcess(
               metadata = EntityMetadata(
                   id = UUID.randomUUID().toString(),
                   audit = AuditInfo(createdBy = "system", updatedBy = "system")
               ),
               displayName = displayName,
               description = description?.let { Description.create(it).orNull() },
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
   )
