package org.blackerp.application.usecases.process

import org.blackerp.domain.core.ad.process.*
import org.blackerp.domain.core.values.*
import org.blackerp.domain.core.metadata.*
import org.blackerp.application.api.dto.requests.CreateProcessRequest 
import arrow.core.*
import java.util.UUID

fun CreateProcessRequest.toDomain(): Either<ProcessError, ADProcess> =
   DisplayName.create(displayName).bimap(
       { ProcessError.ValidationFailed(it.message) },
       { displayName -> 
           ADProcess(
               metadata = EntityMetadata(
                   id = UUID.randomUUID().toString(),
                   audit = AuditInfo(createdBy = "system", updatedBy = "system")
               ),
               displayName = displayName,
               description = null,
               type = ProcessType.valueOf(type.uppercase()),
               parameters = parameters.map { param ->
                   ProcessParameter(
                       id = UUID.randomUUID(),
                       name = param.name,
                       displayName = param.displayName,
                       description = param.description,
                       parameterType = ParameterType.STRING,
                       isMandatory = param.mandatory,
                       validationRule = null
                   )
               },
               implementation = ProcessImplementation.JavaClass("process"),
               schedule = null 
           )
       }
   )
