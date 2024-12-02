
package org.blackerp.domain.ad.reference

import arrow.core.Either
import arrow.core.right
import org.blackerp.domain.EntityMetadata
import org.blackerp.domain.ad.ADObject
import org.blackerp.domain.ad.reference.value.ReferenceName
import org.blackerp.domain.values.DisplayName
import org.blackerp.domain.values.Description
import org.blackerp.shared.ValidationError

data class ADReference(
    override val metadata: EntityMetadata,
    val name: ReferenceName,
    override val displayName: DisplayName,
    override val description: Description?,
    val type: ReferenceType,
    val validationRule: ValidationRule?
) : ADObject {
    companion object {
        fun create(params: CreateReferenceParams): Either<ReferenceError, ADReference> =
            ADReference(
                metadata = params.metadata,
                name = params.name,
                displayName = params.displayName,
                description = params.description,
                type = params.type,
                validationRule = params.validationRule
            ).right()
    }
}

data class ValidationRule(
    val expression: String,
    val errorMessage: String
)

sealed interface ReferenceType {
    object List : ReferenceType
    data class Table(val tableName: String, val keyColumn: String, val displayColumn: String) : ReferenceType
    object Search : ReferenceType
    data class Custom(val validatorClass: String) : ReferenceType
}

data class CreateReferenceParams(
    val metadata: EntityMetadata,
    val name: ReferenceName,
    val displayName: DisplayName,
    val description: Description?,
    val type: ReferenceType,
    val validationRule: ValidationRule? = null
)

sealed class ReferenceError {
    data class ValidationFailed(val errors: List<ValidationError>) : ReferenceError()
    data class DuplicateReference(val name: String) : ReferenceError()
    data class ReferenceNotFound(val name: String) : ReferenceError()
}
