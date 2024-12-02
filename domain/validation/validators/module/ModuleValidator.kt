package org.blackerp.domain.validation.validators.module
 
import arrow.core.Either
import arrow.core.left
import arrow.core.right
import org.blackerp.domain.ad.ADModule
import org.blackerp.shared.ValidationError
import org.blackerp.validation.Validator

class ModuleValidator : Validator<ADModule> {
    override suspend fun validate(entity: ADModule): Either<ValidationError, ADModule> {
        val errors = mutableListOf<ValidationError>()

        // Validate module name format
        if (!entity.name.value.matches(Regex("^[a-z][a-z0-9-]*$"))) {
            errors.add(ValidationError.InvalidFormat(
                "Module name must start with lowercase letter and contain only lowercase letters, numbers, and hyphens"
            ))
        }

        // Validate display name
        if (entity.displayName.value.isBlank()) {
            errors.add(ValidationError.Required("display name"))
        }

        // Validate description length if present
        entity.description?.value?.let { desc ->
            if (desc.length > 255) {
                errors.add(ValidationError.InvalidLength("description", 0, 255))
            }
        }

        return if (errors.isEmpty()) {
            entity.right()
        } else {
            errors.first().left()
        }
    }
}
