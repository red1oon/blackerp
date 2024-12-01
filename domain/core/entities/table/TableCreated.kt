package org.blackerp.domain.table

import org.blackerp.domain.event.DomainEvent
import org.blackerp.domain.event.EventMetadata
import java.util.UUID

data class TableCreated(
    override val metadata: EventMetadata,
    val tableId: UUID,
    val tableName: String
) : DomainEvent
