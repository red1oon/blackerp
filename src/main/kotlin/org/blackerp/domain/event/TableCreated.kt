package org.blackerp.domain.event

import java.util.UUID

data class TableCreated(
    override val metadata: EventMetadata,
    val tableId: UUID,
    val tableName: String
) : DomainEvent
