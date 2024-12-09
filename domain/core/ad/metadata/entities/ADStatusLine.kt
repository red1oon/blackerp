package org.blackerp.domain.core.ad.metadata.entities

import org.blackerp.domain.core.shared.EntityMetadata
import org.blackerp.domain.core.values.*
import org.blackerp.domain.core.ad.base.ADObject
import java.util.UUID

/**
 * Defines document status transitions and rules
 * Part of the AD metadata-driven document lifecycle framework
 */
data class ADStatusLine(
    override val metadata: EntityMetadata,
    private val uuid: UUID = UUID.randomUUID(),
    override val displayName: DisplayName,
    override val description: Description?,
    val documentType: String,        // Target document type
    val fromStatus: String,          // Source status
    val toStatus: String,            // Target status
    val roleId: UUID?,               // Required role for transition
    val conditions: List<String>,    // Rule IDs to evaluate
    val sequence: Int = 0,           // Processing sequence
    val isActive: Boolean = true
) : ADObject {
    override val id: String 
        get() = uuid.toString()
}
