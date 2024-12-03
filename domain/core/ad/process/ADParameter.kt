package org.blackerp.domain.core.ad.process

import org.blackerp.domain.core.values.DataType
import java.util.UUID

data class ADParameter(
    val id: UUID,
    val name: String,
    val displayName: String,
    val description: String?,
    val dataType: DataType,
    val mandatory: Boolean = false,
    val defaultValue: String? = null,
    val validation: ParameterValidation? = null
)

data class ParameterValidation(
    val expression: String,
    val errorMessage: String
)
