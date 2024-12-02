package org.blackerp.domain.validation.validators.model

import org.springframework.stereotype.Component
import org.blackerp.domain.core.error.TableError
import arrow.core.Either
import arrow.core.left
import arrow.core.right

@Component
class CompositeValidator<T>(
    private val validators: List<Validator<T>>
) {
    fun validate(target: T): Either<TableError, T> {
        val violations = mutableListOf<TableError.Violation>()
        
        validators.forEach { validator ->
            validator.validate(target).fold(
                { error -> 
                    when (error) {
                        is TableError.ValidationError -> violations.addAll(error.violations)
                        else -> return error.left()
                    }
                },
                { /* continue validation */ }
            )
        }
        
        return if (violations.isEmpty()) {
            target.right()
        } else {
            TableError.ValidationError(
                message = "Validation failed",
                violations = violations
            ).left()
        }
    }
}
