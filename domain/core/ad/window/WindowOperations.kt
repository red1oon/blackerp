
package org.blackerp.domain.core.ad.window

import arrow.core.Either
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface WindowOperations {
    suspend fun save(window: ADWindow): Either<WindowError, ADWindow>
    suspend fun findById(id: UUID): Either<WindowError, ADWindow?>
    suspend fun findByName(name: WindowName): Either<WindowError, ADWindow?>
    suspend fun search(query: String, pageSize: Int = 20, page: Int = 0): Flow<ADWindow>
    suspend fun delete(id: UUID): Either<WindowError, Unit>
    suspend fun validateWindow(window: ADWindow): Either<WindowError, Unit>
}

interface WindowRepository {
    suspend fun save(window: ADWindow): Either<WindowError, ADWindow>
    suspend fun findById(id: UUID): Either<WindowError, ADWindow?>
    suspend fun findByName(name: WindowName): Either<WindowError, ADWindow?>
    suspend fun search(query: String, pageSize: Int, page: Int): Flow<ADWindow>
    suspend fun delete(id: UUID): Either<WindowError, Unit>
}
