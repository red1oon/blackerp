package org.blackerp.domain.core.ad.tab

import arrow.core.Either
import arrow.core.right
import org.blackerp.domain.core.shared.EntityMetadata
import org.blackerp.domain.core.ad.base.ADObject
import org.blackerp.domain.core.values.*
import org.blackerp.domain.core.ad.window.WindowField
import java.util.UUID

/**
 * Represents a Tab within a Window. Tabs are containers for fields and 
 * provide data view/entry capabilities.
 */
data class ADTab(
    override val metadata: EntityMetadata,
    private val uuid: UUID = UUID.randomUUID(),
    override val displayName: DisplayName,
    override val description: Description?,
    val name: TabName,
    val tableId: UUID,
    val sequence: Int = 10,
    val fields: List<WindowField> = emptyList(),
    val readOnly: Boolean = false,
    val singleRow: Boolean = false,
    val orderByClause: String? = null
) : ADObject {
    override val id: String get() = uuid.toString()

    companion object {
        fun create(
            metadata: EntityMetadata,
            displayName: DisplayName,
            name: TabName,
            description: Description?,
            tableId: UUID,
            sequence: Int = 10
        ): Either<TabError, ADTab> {
            return ADTab(
                metadata = metadata,
                displayName = displayName,
                description = description,
                name = name,
                tableId = tableId,
                sequence = sequence
            ).right()
        }
    }
}
