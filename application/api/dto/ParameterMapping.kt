package org.blackerp.application.api.dto

import org.blackerp.domain.core.ad.process.ProcessParameter as DomainProcessParameter
import org.blackerp.domain.core.ad.process.ParameterType
import org.blackerp.domain.core.values.DataType
import java.util.UUID

fun toDomainParameter(dto: org.blackerp.application.api.process.ProcessParameter): DomainProcessParameter {
   return DomainProcessParameter(
       id = UUID.randomUUID(),
       name = dto.name,
       displayName = dto.displayName,
       description = dto.description,
       parameterType = when(dto.dataType) {
           DataType.STRING -> ParameterType.STRING
           DataType.INTEGER -> ParameterType.NUMBER 
           DataType.DATE -> ParameterType.DATE
           DataType.BOOLEAN -> ParameterType.BOOLEAN
           else -> ParameterType.STRING
       },
       isMandatory = dto.mandatory,
       validationRule = null
   )
}
