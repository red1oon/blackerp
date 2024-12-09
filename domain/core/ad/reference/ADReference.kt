package org.blackerp.domain.core.ad.reference

import org.blackerp.domain.core.shared.EntityMetadata
import org.blackerp.domain.core.ad.base.ADObject
import org.blackerp.domain.core.values.DisplayName
import org.blackerp.domain.core.values.Description
import org.blackerp.domain.core.shared.ValidationError
import arrow.core.Either
import arrow.core.right
import java.util.UUID

data class ADReference(
    override val metadata: EntityMetadata,
    private val uuid: UUID = UUID.randomUUID(),
    override val displayName: DisplayName,
    override val description: Description?,
    val type: ReferenceType,
    val validationRule: ValidationRule?,
    val isActive: Boolean = true,
    val parentId: UUID? = null,
    val sortOrder: Int = 0
) : ADObject {
    override val id: String 
        get() = uuid.toString()
}


data class ValidationRule(
    val expression: String,
    val errorMessage: String,
    val parameters: Map<String, String> = emptyMap()
)
