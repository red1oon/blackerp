package org.blackerp.domain.core.ad.tab

import org.blackerp.domain.core.DomainException
import org.blackerp.domain.core.shared.ValidationError

sealed class TabError(message: String) : DomainException(message) {
    data class ValidationFailed(val errors: List<ValidationError>) :  
        TabError("Validation failed: ${errors.joinToString { it.message?.toString() ?: "Unknown error" }}")

    data class NotFound(val id: String) : 
        TabError("Tab not found: $id")
    
    data class DuplicateTab(val name: String) : 
        TabError("Tab already exists: $name")
}
