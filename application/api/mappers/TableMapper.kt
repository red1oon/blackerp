// application/api/mappers/TableMapper.kt
package org.blackerp.application.api.mappers

import org.springframework.stereotype.Component
import org.blackerp.application.api.dto.requests.CreateTableRequest
import org.blackerp.domain.core.ad.table.ADTable
import org.blackerp.domain.core.metadata.EntityMetadata
import org.blackerp.domain.core.values.*
import org.blackerp.domain.core.metadata.AuditInfo
import org.blackerp.domain.core.shared.ValidationError
import arrow.core.Either
import java.util.UUID

@Component
class TableMapper {
    fun toDomain(request: CreateTableRequest): Either<ValidationError, ADTable> =
        TableName.create(request.name).map { tableName ->
            ADTable(
                metadata = EntityMetadata(
                    id = UUID.randomUUID().toString(),
                    audit = AuditInfo(
                        createdBy = "system", 
                        updatedBy = "system"
                    )
                ),
                name = tableName,
                displayName = DisplayName.create(request.displayName).fold(
                    { throw IllegalArgumentException(it.message) },
                    { it }
                ),
                description = request.description?.let { desc ->
                    Description.create(desc).fold(
                        { throw IllegalArgumentException(it.message) },
                        { it }
                    )
                },
                accessLevel = AccessLevel.valueOf(request.accessLevel.uppercase())
            )
        }
}
