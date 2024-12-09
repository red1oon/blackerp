package org.blackerp.domain.core.ad.metadata.entities

import org.blackerp.domain.core.shared.EntityMetadata
import org.blackerp.domain.core.values.*
import org.blackerp.domain.core.ad.base.ADObject
import java.util.UUID

/**
 * Defines field-level validation rules
 * Part of the AD metadata-driven validation framework
 */
data class ADValidationRule(
    override val metadata: EntityMetadata,
    private val uuid: UUID = UUID.randomUUID(),
    override val displayName: DisplayName,
    override val description: Description?,
    val entityType: String,          // Target entity type
    val fieldName: String,           // Target field name
    val expression: String,          // Validation expression
    val errorMessage: String,        // Error message on validation failure
    val isActive: Boolean = true
) : ADObject {
    override val id: String 
        get() = uuid.toString()
}
