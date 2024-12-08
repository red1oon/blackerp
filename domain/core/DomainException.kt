// domain/core/DomainException.kt
package org.blackerp.domain.core

import arrow.core.Either
import arrow.core.left

abstract class DomainException(message: String) : Exception(message) {
    fun <T> toEither(): Either<DomainException, T> = this.left()
}
