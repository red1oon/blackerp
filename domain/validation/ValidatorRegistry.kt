// domain/validation/ValidatorRegistry.kt
package org.blackerp.domain.validation

interface ValidatorRegistry {
    fun <T> getValidator(type: Class<T>): Validator<T>?
}
