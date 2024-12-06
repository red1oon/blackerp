package org.blackerp.domain.core.shared

sealed class ValidationError(val message: String) {
    data class InvalidFormat(val error: String) : ValidationError(error)
}
