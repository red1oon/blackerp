package org.blackerp.domain.core.security.metadata

import arrow.core.Either
import kotlinx.coroutines.flow.Flow
import org.blackerp.domain.core.security.SecurityError
import java.util.UUID

interface SecurityRuleRepository {
    suspend fun save(rule: SecurityRuleDefinition): Either<SecurityError, SecurityRuleDefinition>
    suspend fun findByEntity(entityType: String, entityId: UUID): Flow<SecurityRuleDefinition>
    suspend fun findByRole(roleId: UUID): Flow<SecurityRuleDefinition>
    suspend fun validateRule(rule: SecurityRuleDefinition): Either<SecurityError, Unit>
    suspend fun delete(id: UUID): Either<SecurityError, Unit>
}
