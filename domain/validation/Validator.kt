// domain/validation/Validator.kt
package org.blackerp.domain.validation

import arrow.core.Either
import org.blackerp.domain.core.shared.ValidationError

interface Validator<T> {
    suspend fun validate(entity: T): Either<ValidationError, T>
}
