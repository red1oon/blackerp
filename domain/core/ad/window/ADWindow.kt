package org.blackerp.domain.core.ad.window

import arrow.core.Either
import arrow.core.right
import org.blackerp.domain.core.shared.EntityMetadata
import org.blackerp.domain.core.ad.base.ADObject
import org.blackerp.domain.core.values.DisplayName
import org.blackerp.domain.core.values.Description
import org.blackerp.domain.core.ad.tab.ADTab
import java.util.UUID

data class ADWindow(
    override val metadata: EntityMetadata,
    private val uuid: UUID = UUID.randomUUID(),
    override val displayName: DisplayName,
    override val description: Description?,
    val name: WindowName,
    val tabs: List<ADTab>,
    val isActive: Boolean = true,
    val isSOTrx: Boolean = true,  // Sales/Purchase indicator
    val windowType: WindowType = WindowType.MAINTAIN
) : ADObject {
    override val id: String get() = uuid.toString()
}

enum class WindowType {
    MAINTAIN,    // Regular maintenance window
    QUERY,       // Query/lookup window
    TRANSACTION  // Transaction entry window
}

data class WindowField(
    val id: UUID = UUID.randomUUID(),
    val columnName: String,
    val displayName: String,
    val description: String?,
    val isDisplayed: Boolean = true,
    val isReadOnly: Boolean = false,
    val isMandatory: Boolean = false,
    val sequence: Int = 0,
    val displayLogic: String? = null,  // Display condition
    val defaultValue: String? = null,
    val validationRule: String? = null
)

sealed class WindowError(message: String) : Exception(message) {
    data class ValidationFailed(val details: String) : WindowError("Window validation failed: $details")
    data class NotFound(val id: UUID) : WindowError("Window not found: $id")
    data class TabError(val details: String) : WindowError("Tab error: $details")
}
