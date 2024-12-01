
package org.blackerp.domain.table

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import org.blackerp.domain.DomainEntity
import org.blackerp.domain.EntityMetadata
import org.blackerp.domain.values.ColumnName
import org.blackerp.domain.values.DisplayName
import org.blackerp.domain.values.Description
import org.blackerp.domain.values.DataType
import org.blackerp.domain.values.Length
import org.blackerp.domain.values.Precision
import org.blackerp.domain.values.Scale
import org.blackerp.shared.ValidationError

data class ColumnDefinition(
    override val metadata: EntityMetadata,
    val name: ColumnName,
    val displayName: DisplayName,
    val description: Description?,
    val dataType: DataType,
    val length: Length?,
    val precision: Precision?,
    val scale: Scale?,
    val mandatory: Boolean = false,
    val defaultValue: String? = null
) : DomainEntity {

    companion object {
        fun create(params: CreateColumnParams): Either<ColumnError, ColumnDefinition> {
            val errors = mutableListOf<ValidationError>()

            when (params.dataType) {
                DataType.STRING -> {
                    if (params.length == null) 
                        errors.add(ValidationError.Required("length for string type"))
                    if (params.precision != null || params.scale != null) 
                        errors.add(ValidationError.InvalidValue("string type cannot have precision or scale"))
                }
                DataType.DECIMAL -> {
                    if (params.precision == null) 
                        errors.add(ValidationError.Required("precision for decimal type"))
                    if (params.length != null) 
                        errors.add(ValidationError.InvalidValue("decimal type cannot have length"))
                }
                else -> {
                    if (params.length != null || params.precision != null || params.scale != null) 
                        errors.add(ValidationError.InvalidValue("type ${params.dataType} cannot have length, precision, or scale"))
                }
            }

            if (errors.isNotEmpty()) {
                return ColumnError.ValidationFailed(errors).left()
            }

            return ColumnDefinition(
                metadata = params.metadata,
                name = params.name,
                displayName = params.displayName,
                description = params.description,
                dataType = params.dataType,
                length = params.length,
                precision = params.precision,
                scale = params.scale,
                mandatory = params.mandatory,
                defaultValue = params.defaultValue
            ).right()
        }
    }
}

sealed interface ColumnError {
    data class ValidationFailed(val errors: List<ValidationError>) : ColumnError
    data class NotFound(val columnName: String) : ColumnError
}
