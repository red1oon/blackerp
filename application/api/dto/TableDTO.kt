package org.blackerp.application.api.dto

import org.blackerp.domain.core.ad.table.ADTable
import org.blackerp.domain.core.values.*
import org.blackerp.domain.core.metadata.*
import org.blackerp.domain.core.shared.ValidationError
import java.util.UUID
import arrow.core.Either
import arrow.core.getOrElse

data class TableDTO(
    val id: String? = null,
    val name: String,
    val displayName: String,
    val description: String?,
    val accessLevel: String
) {
    companion object {
        fun fromDomain(table: ADTable) = TableDTO(
            id = table.id,
            name = table.name.value,
            displayName = table.displayName.value,
            description = table.description?.value,
            accessLevel = table.accessLevel.name
        )
    }

    fun toDomain(): Either<ValidationError, ADTable> =
        TableName.create(name).map { tableName ->
            ADTable(
                metadata = EntityMetadata(
                    id = id ?: UUID.randomUUID().toString(),
                    audit = AuditInfo(
                        createdBy = "system",
                        updatedBy = "system"
                    )
                ),
                name = tableName,
                displayName = DisplayName.create(displayName).getOrElse { 
                    throw IllegalArgumentException("Invalid display name") 
                },
                description = description?.let {
                    Description.create(it).getOrElse { null }
                },
                accessLevel = AccessLevel.valueOf(accessLevel)
            )
        }
}
