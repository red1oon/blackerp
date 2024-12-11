package org.blackerp.application.services.security

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.blackerp.domain.core.security.metadata.*
import org.blackerp.domain.core.error.SecurityError
import org.blackerp.domain.core.security.SecurityContext
import arrow.core.Either
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Service
class SecurityMetadataService(
    private val securityRuleRepository: SecurityRuleRepository,
    private val securityContext: SecurityContext
) {
    @Transactional
    suspend fun createSecurityRule(rule: SecurityRuleDefinition): Either<SecurityError, SecurityRuleDefinition> =
        securityRuleRepository.validateRule(rule)
            .flatMap { securityRuleRepository.save(rule) }

    @Transactional(readOnly = true)
    suspend fun getEntityRules(entityType: String, entityId: UUID): Flow<SecurityRuleDefinition> =
        securityRuleRepository.findByEntity(entityType, entityId)

    @Transactional
    suspend fun validateAccess(
        entityType: String,
        entityId: UUID,
        requiredPermission: SecurityRuleType
    ): Either<SecurityError, Unit> {
        // Implement AD metadata-driven access validation
        return Unit.right()
    }
}
