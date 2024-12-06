package org.blackerp.domain.core.ad.table

import arrow.core.Either
import arrow.core.right
import org.blackerp.domain.core.shared.EntityMetadata
import org.blackerp.domain.core.values.*
import org.blackerp.domain.core.ad.base.ADObject
import org.blackerp.domain.core.error.TableError
import java.util.UUID

data class ADTable(
    override val metadata: EntityMetadata,
    private val uuid: UUID = UUID.randomUUID(),
    override val displayName: DisplayName,
    override val description: Description?,
    val name: TableName,
    val accessLevel: AccessLevel,
    val columns: List<ColumnDefinition> = emptyList(),
    val constraints: List<TableConstraint> = emptyList()
) : ADObject {
    override val id: String get() = uuid.toString()
}
