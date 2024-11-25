package org.blackerp.api.dto

import java.util.UUID

data class TableResponse(
    val id: UUID,
    val name: String,
    val displayName: String,
    val description: String?,
    val accessLevel: String
)
