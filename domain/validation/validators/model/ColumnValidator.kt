package org.blackerp.domain.validation.validators.model
 
import arrow.core.Either
import arrow.core.left
import arrow.core.right
import org.blackerp.domain.table.ColumnDefinition
import org.blackerp.domain.values.DataType
import org.blackerp.shared.ValidationError
import org.blackerp.validation.Validator

class ColumnValidator : Validator<ColumnDefinition> {
    override suspend fun validate(entity: ColumnDefinition): Either<ValidationError, ColumnDefinition> {
        val errors = mutableListOf<ValidationError>()

        // Validate name format
        if (!entity.name.value.matches(Regex("^[a-z][a-z0-9_]*$"))) {
            errors.add(ValidationError.InvalidFormat(
                "Column name must start with lowercase letter and contain only lowercase letters, numbers, and underscores"
            ))
        }

        // Validate type-specific constraints
        when (entity.dataType) {
            DataType.STRING -> {
                if (entity.length == null) {
                    errors.add(ValidationError.Required("length for string type"))
                }
                if (entity.precision != null || entity.scale != null) {
                    errors.add(ValidationError.InvalidValue("string type cannot have precision or scale"))
                }
            }
            DataType.DECIMAL -> {
                if (entity.precision == null) {
                    errors.add(ValidationError.Required("precision for decimal type"))
                }
                if (entity.length != null) {
                    errors.add(ValidationError.InvalidValue("decimal type cannot have length"))
                }
            }
            else -> {
                if (entity.length != null || entity.precision != null || entity.scale != null) {
                    errors.add(ValidationError.InvalidValue("type ${entity.dataType} cannot have length, precision, or scale"))
                }
            }
        }

        return if (errors.isEmpty()) {
            entity.right()
        } else {
            errors.first().left()
        }
    }
}
