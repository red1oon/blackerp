// File: src/main/kotlin/org/blackerp/shared/ValidationError.kt
package org.blackerp.shared

sealed class ValidationError(val message: String) {
    data class InvalidFormat(val details: String) : ValidationError(details)
    data class Required(val field: String) : ValidationError("Field $field is required")
    data class InvalidLength(val field: String, val min: Int, val max: Int) : 
        ValidationError("Field $field must be between $min and $max characters")
    data class InvalidValue(val details: String) : ValidationError(details)
}
