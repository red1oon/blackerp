package org.blackerp.application.api.process

import org.blackerp.domain.core.values.DataType
import org.blackerp.domain.core.ad.process.*
import java.util.UUID

interface Parameter {
   val id: UUID
   val name: String
   val displayName: String
   val description: String?
   val parameterType: ParameterType
   val isMandatory: Boolean
   val validationRule: String?
}

data class DomainProcessParameter(
   override val name: String,
   override val displayName: String,
   val dataType: DataType,
   val mandatory: Boolean = false,
   override val description: String? = null
) : Parameter {
   override val id = UUID.randomUUID()
   override val parameterType = toParameterType(dataType)
   override val isMandatory = mandatory
   override val validationRule: String? = null

   private fun toParameterType(type: DataType): ParameterType = when(type) {
       DataType.STRING -> ParameterType.STRING
       DataType.INTEGER -> ParameterType.NUMBER
       DataType.DATE -> ParameterType.DATE
       DataType.BOOLEAN -> ParameterType.BOOLEAN
       else -> ParameterType.STRING
   }
}
