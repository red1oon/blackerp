package org.blackerp.domain.core.ad.metadata.entities

import org.blackerp.domain.core.shared.EntityMetadata
import org.blackerp.domain.core.values.*
import org.blackerp.domain.core.ad.base.ADObject
import java.util.UUID

/**
 * Defines a business rule that can be evaluated at runtime
 * Part of the AD metadata-driven validation framework
 */
data class ADRule(
    override val metadata: EntityMetadata,
    private val uuid: UUID = UUID.randomUUID(),
    override val displayName: DisplayName,
    override val description: Description?,
    val ruleType: String,            // e.g. VALIDATION, CALCULATION, WORKFLOW
    val entityType: String,          // Target entity type
    val expression: String,          // Rule expression/script
    val errorMessage: String?,       // Custom error message
    val parameters: List<RuleParameter> = emptyList()
) : ADObject {
    override val id: String 
        get() = uuid.toString()
}

data class RuleParameter(
    val name: String,
    val dataType: DataType,
    val mandatory: Boolean = false,
    val defaultValue: String? = null
)
