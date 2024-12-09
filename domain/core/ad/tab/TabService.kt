package org.blackerp.domain.core.ad.tab

import arrow.core.Either
import kotlinx.coroutines.flow.Flow
import org.blackerp.domain.core.values.TabName
import java.util.UUID

interface TabService {
    suspend fun createTab(command: CreateTabCommand): Either<TabError, ADTab>
    suspend fun updateTab(id: UUID, command: UpdateTabCommand): Either<TabError, ADTab>
    suspend fun findByName(name: TabName): Either<TabError, ADTab?>
    suspend fun findById(id: UUID): Either<TabError, ADTab?>
    suspend fun listTabs(criteria: TabSearchCriteria): Flow<ADTab>
    suspend fun deleteTab(id: UUID): Either<TabError, Unit>
}

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

data class TabSearchCriteria(
    val windowId: UUID? = null,
    val tableId: UUID? = null,
    val namePattern: String? = null,
    val pageSize: Int = 20,
    val page: Int = 0
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
