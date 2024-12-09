// domain/validation/BaseValidator.kt
package org.blackerp.domain.validation

import arrow.core.Either
import arrow.core.right
import org.blackerp.domain.core.shared.ValidationError

abstract class BaseValidator<T> : Validator<T> {
    override suspend fun validate(entity: T): Either<ValidationError, T> = entity.right()
}
