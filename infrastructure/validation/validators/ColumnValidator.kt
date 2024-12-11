package org.blackerp.infrastructure.validation.validators

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import org.blackerp.domain.core.ad.table.ColumnDefinition
import org.blackerp.domain.core.values.DataType
import org.blackerp.domain.core.shared.ValidationError
import org.blackerp.domain.core.validation.Validator

class ColumnValidator : Validator<ColumnDefinition> {
    override suspend fun validate(entity: ColumnDefinition): Either<ValidationError, ColumnDefinition> {
        val errors = mutableListOf<ValidationError>()

        // Validate name format
        if (!entity.name.matches(Regex("^[a-z][a-z0-9_]*$"))) {
            errors.add(ValidationError.InvalidFormat(
                "Column name must start with lowercase letter and contain only lowercase letters, numbers, and underscores"
            ))
        }

        // Validate type-specific constraints
        when (entity.dataType) {
            "STRING" -> {
                if (entity.length == null) {
                    errors.add(ValidationError.Required("length for string type"))
                }
            }
            "DECIMAL" -> {
                if (entity.precision == null) {
                    errors.add(ValidationError.Required("precision for decimal type"))
                }
            }
            else -> {
                if (entity.length != null || entity.precision != null || entity.scale != null) {
                    errors.add(ValidationError.InvalidValue(
                        "type ${entity.dataType} cannot have length, precision, or scale"
                    ))
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
