package org.blackerp.domain.core.ad.table 

import org.blackerp.domain.core.values.*

data class CreateTableCommand(
    val name: TableName,
    val displayName: DisplayName,
    val description: Description? = null,
    val accessLevel: AccessLevel,
    val columns: List<CreateColumnCommand>
)

data class UpdateTableCommand(
    val displayName: DisplayName? = null,
    val description: Description? = null,
    val columns: List<UpdateColumnCommand>? = null
)

data class CreateColumnCommand(
    val name: String,
    val displayName: String,
    val description: String?,
    val dataType: String,
    val mandatory: Boolean = false,
    val length: Int? = null,
    val precision: Int? = null,
    val scale: Int? = null
)

data class UpdateColumnCommand(
    val name: String?,
    val displayName: String?,
    val description: String?,
    val mandatory: Boolean?
)