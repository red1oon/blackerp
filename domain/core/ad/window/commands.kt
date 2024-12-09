// File: domain/core/ad/window/commands.kt
package org.blackerp.domain.core.ad.window

import org.blackerp.domain.core.values.*
import java.util.UUID

// Create commands
data class CreateWindowCommand(
    val name: WindowName,
    val displayName: DisplayName,
    val description: Description? = null,
    val tabs: List<CreateTabCommand> = emptyList(),
    val isActive: Boolean? = null,
    val isSOTrx: Boolean? = null,
    val windowType: WindowType? = null
)

data class UpdateWindowCommand(
    val displayName: DisplayName? = null,
    val description: Description? = null,
    val isActive: Boolean? = null,
    val isSOTrx: Boolean? = null
)

data class CreateTabCommand(
    val displayName: String,
    val name: String,
    val description: String?,
    val tableId: UUID,
    val sequence: Int = 10,
    val fields: List<CreateFieldCommand> = emptyList()
)

data class UpdateTabCommand(
    val displayName: String?,
    val description: String?,
    val sequence: Int?,
    val fields: List<UpdateFieldCommand>?
)

data class CreateFieldCommand(
    val columnName: String,
    val displayName: String,
    val description: String?,
    val sequence: Int = 10,
    val isDisplayed: Boolean = true,
    val isReadOnly: Boolean = false,
    val isMandatory: Boolean = false
)

data class UpdateFieldCommand(
    val id: UUID,
    val displayName: String?,
    val description: String?,
    val sequence: Int?,
    val isDisplayed: Boolean?,
    val isReadOnly: Boolean?,
    val isMandatory: Boolean?
)

// Search criteria
data class TabSearchCriteria(
    val windowId: UUID? = null,
    val tableId: UUID? = null,
    val namePattern: String? = null,
    val pageSize: Int = 20,
    val page: Int = 0
)