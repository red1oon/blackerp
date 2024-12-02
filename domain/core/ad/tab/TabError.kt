
package org.blackerp.domain.ad.tab

import org.blackerp.domain.DomainException
import org.blackerp.shared.ValidationError

sealed class TabError(message: String) : DomainException(message) {
    data class ValidationFailed(val errors: List<ValidationError>) : 
        TabError("Validation failed: ${errors.joinToString { it.message }}")
    
    data class NotFound(val id: String) : 
        TabError("Tab not found: $id")
    
    data class DuplicateTab(val name: String) : 
        TabError("Tab already exists: $name")
}
