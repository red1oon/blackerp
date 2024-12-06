package org.blackerp.domain.core.ad.base

import arrow.core.Either
import kotlinx.coroutines.flow.Flow
import org.blackerp.domain.core.error.DomainError
import org.blackerp.domain.core.values.DisplayName
import org.blackerp.domain.core.values.Description
import org.blackerp.domain.core.Version
import java.util.UUID

interface ModuleService {
    suspend fun createModule(command: CreateModuleCommand): Either<DomainError, ADModule>
    suspend fun updateModule(id: UUID, command: UpdateModuleCommand): Either<DomainError, ADModule>
    suspend fun getModules(active: Boolean? = null): Flow<ADModule>
}

data class CreateModuleCommand(
    val name: ModuleName,
    val displayName: DisplayName,
    val description: Description? = null,
    val version: Version,
    val dependencies: Set<ModuleDependency> = emptySet()
)

data class UpdateModuleCommand(
    val displayName: DisplayName? = null,
    val description: Description? = null,
    val version: Version? = null
)

data class ModuleDependency(
    val moduleName: ModuleName,
    val version: Version
)
