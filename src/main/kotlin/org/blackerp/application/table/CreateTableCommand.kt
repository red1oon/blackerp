package org.blackerp.application.table

import org.blackerp.domain.values.AccessLevel
import org.blackerp.domain.values.DataType

// CreateTableCommand with added columns property

data class CreateTableCommand(
    val name: String,
    val displayName: String,
    val description: String?,
    val accessLevel: AccessLevel,
    val createdBy: String,
    val columns: List<CreateColumnCommand>  
)

data class CreateColumnCommand(
    val name: String,
    val dataType: DataType,
    val length: Int?,
    val precision: Int?,
    val displayName: String,  
    val description: String?,
    val scale: Int?
)
