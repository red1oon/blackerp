// File: domain/core/ad/window/WindowService.kt
package org.blackerp.domain.core.ad.window

import arrow.core.Either
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import org.blackerp.domain.core.ad.tab.ADTab

interface WindowService {
    suspend fun createWindow(command: CreateWindowCommand): Either<WindowError, ADWindow>
    suspend fun updateWindow(id: UUID, command: UpdateWindowCommand): Either<WindowError, ADWindow>
    suspend fun findByName(name: WindowName): Either<WindowError, ADWindow?>
    suspend fun findById(id: UUID): Either<WindowError, ADWindow?>
    suspend fun listTabs(criteria: TabSearchCriteria): Flow<ADTab>
    suspend fun deleteWindow(id: UUID): Either<WindowError, Unit>
}