package org.blackerp.application.api.dto.requests

import org.blackerp.domain.core.ad.table.*
import org.blackerp.domain.core.values.*
import org.blackerp.domain.core.shared.ValidationError
import org.blackerp.domain.core.metadata.*
import arrow.core.*
import java.util.UUID

data class CreateTableRequest(
    val name: String,
    val displayName: String,
    val description: String?,
    val accessLevel: String
) {
    fun toDomain(): Either<ValidationError, ADTable> =
        TableName.create(name).flatMap { tableName ->
            DisplayName.create(displayName).flatMap { dispName ->
                val descriptionResult = description?.let { desc ->
                    Description.create(desc)
                } ?: Either.Right(null)
                
                descriptionResult.map { desc ->
                    ADTable(
                        metadata = EntityMetadata(
                            id = UUID.randomUUID().toString(),
                            audit = AuditInfo(createdBy = "system", updatedBy = "system")
                        ),
                        displayName = dispName,
                        description = desc,
                        name = tableName,
                        accessLevel = AccessLevel.valueOf(accessLevel.uppercase())
                    )
                }
            }
        }
}
