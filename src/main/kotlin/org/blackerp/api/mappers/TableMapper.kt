// File: src/main/kotlin/org/blackerp/api/mappers/TableMapper.kt

package org.blackerp.api.mappers

import org.springframework.stereotype.Component
import org.blackerp.api.dto.request.CreateTableRequest
import org.blackerp.api.dto.request.CreateColumnRequest
import org.blackerp.api.dto.response.TableResponse
import org.blackerp.application.table.CreateTableCommand
import org.blackerp.application.table.CreateColumnCommand
import org.blackerp.domain.table.ADTable
import org.blackerp.domain.values.AccessLevel
import org.blackerp.domain.values.DataType

@Component
class TableMapper {
    fun toCommand(request: CreateTableRequest): CreateTableCommand =
        CreateTableCommand(
            name = request.name,
            displayName = request.displayName,
            description = request.description,
            accessLevel = AccessLevel.valueOf(request.accessLevel.uppercase()),
            createdBy = "system",
            columns = request.columns.map { toColumnCommand(it) }
        )

    private fun toColumnCommand(request: CreateColumnRequest): CreateColumnCommand =
        CreateColumnCommand(
            name = request.name,
            displayName = request.displayName,
            description = request.description,
            dataType = DataType.valueOf(request.dataType.uppercase()),
            length = request.length,
            precision = request.precision,
            scale = request.scale
        )

    fun toResponse(table: ADTable): TableResponse =
        TableResponse(
            id = table.metadata.id,
            name = table.name.value,
            displayName = table.displayName.value,
            description = table.description?.value,
            accessLevel = table.accessLevel.name
        )
}