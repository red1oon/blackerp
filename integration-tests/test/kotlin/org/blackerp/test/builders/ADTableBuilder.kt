package org.blackerp.test.builders

import org.blackerp.domain.core.ad.table.*
import org.blackerp.domain.core.shared.EntityMetadata
import org.blackerp.domain.core.shared.AuditInfo
import org.blackerp.domain.core.values.*
import java.util.UUID

class ADTableBuilder {
    private var name = "test_table"
    private var displayName = "Test Table"
    private var description: String? = null
    private var accessLevel = AccessLevel.ORGANIZATION
    private var columns: List<ColumnDefinition> = emptyList()

    fun withName(name: String) = apply { this.name = name }
    fun withDisplayName(displayName: String) = apply { this.displayName = displayName }
    fun withDescription(description: String?) = apply { this.description = description }
    fun withAccessLevel(accessLevel: AccessLevel) = apply { this.accessLevel = accessLevel }
    fun withColumns(columns: List<ColumnDefinition>) = apply { this.columns = columns }

    fun build(): ADTable {
        return ADTable(
            metadata = EntityMetadata(
                id = UUID.randomUUID().toString(),
                audit = AuditInfo(
                    createdBy = "test",
                    updatedBy = "test"
                )
            ),
            name = TableName.create(name).orNull()!!,
            displayName = DisplayName.create(displayName).orNull()!!,
            description = description?.let { Description.create(it).orNull() },
            accessLevel = accessLevel,
            columns = columns
        )
    }
}
