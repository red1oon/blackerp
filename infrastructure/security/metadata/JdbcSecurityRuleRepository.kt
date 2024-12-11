package org.blackerp.infrastructure.security.metadata

import org.springframework.stereotype.Repository
import org.springframework.jdbc.core.JdbcTemplate
import org.blackerp.domain.core.security.metadata.*
import org.blackerp.domain.core.error.SecurityError
import arrow.core.Either
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.UUID
import org.slf4j.LoggerFactory

@Repository
class JdbcSecurityRuleRepository(
    private val jdbcTemplate: JdbcTemplate
) : SecurityRuleRepository {
    private val logger = LoggerFactory.getLogger(JdbcSecurityRuleRepository::class.java)

    override suspend fun save(rule: SecurityRuleDefinition): Either<SecurityError, SecurityRuleDefinition> {
        return try {
            jdbcTemplate.update("""
                INSERT INTO ad_security_rule (
                    id, entity_type, entity_id, role_id, rule_type,
                    expression, is_active, created_by, updated_by
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                ON CONFLICT (id) DO UPDATE SET
                    expression = EXCLUDED.expression,
                    is_active = EXCLUDED.is_active,
                    updated_by = EXCLUDED.updated_by,
                    updated_at = CURRENT_TIMESTAMP
            """,
                rule.metadata.id,
                rule.entityType,
                rule.entityId,
                rule.roleId,
                rule.ruleType.name,
                rule.expression,
                rule.isActive,
                rule.metadata.audit.createdBy,
                rule.metadata.audit.updatedBy
            )
            rule.right()
        } catch (e: Exception) {
            logger.error("Failed to save security rule: ${rule.id}", e)
            SecurityError.ValidationFailed("Failed to save security rule: ${e.message}").left()
        }
    }

    // Implement other repository methods...
}
