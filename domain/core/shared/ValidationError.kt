// File: domain/core/shared/ValidationError.kt
package org.blackerp.domain.core.shared

sealed class ValidationError(val message: String) {
    data class InvalidFormat(val error: String) : ValidationError(error)
    data class InvalidLength(val field: String, val min: Int, val max: Int) : ValidationError("$field length must be between $min and $max")
    data class InvalidValue(val error: String) : ValidationError(error)
    data class Required(val field: String) : ValidationError("$field is required")
}