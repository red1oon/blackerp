package org.blackerp.infrastructure.persistence.metadata.repositories

import org.springframework.stereotype.Repository
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.transaction.annotation.Transactional
import org.blackerp.domain.core.ad.metadata.entities.*
import org.blackerp.domain.core.ad.metadata.repositories.MetadataRepository
import org.blackerp.domain.core.ad.metadata.services.MetadataError
import org.blackerp.domain.core.shared.EntityMetadata
import org.blackerp.domain.core.shared.AuditInfo
import org.blackerp.domain.core.values.*
import arrow.core.Either
import arrow.core.left
import arrow.core.right
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.UUID
import org.slf4j.LoggerFactory

@Repository
class JdbcMetadataRepository(
    private val jdbcTemplate: JdbcTemplate
) : MetadataRepository {

    private val logger = LoggerFactory.getLogger(JdbcMetadataRepository::class.java)

    @Transactional
    override suspend fun <T : Any> save(entity: T): Either<MetadataError, T> = try {
        when (entity) {
            is ADRule -> saveRule(entity)
            is ADValidationRule -> saveValidationRule(entity)
            is ADStatusLine -> saveStatusLine(entity) 
            else -> throw IllegalArgumentException("Unsupported entity type")
        }.map { entity }
    } catch (e: Exception) {
        logger.error("Failed to save metadata entity", e)
        MetadataError.ValidationFailed("Save failed: ${e.message}").left()
    }

    private suspend fun saveRule(rule: ADRule): Either<MetadataError, Unit> {
        jdbcTemplate.update("""
            INSERT INTO ad_rule (
                id, display_name, description, rule_type, entity_type, 
                expression, error_message, is_active, created_by, updated_by
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT (id) DO UPDATE SET
                display_name = EXCLUDED.display_name,
                description = EXCLUDED.description,
                rule_type = EXCLUDED.rule_type,
                entity_type = EXCLUDED.entity_type,
                expression = EXCLUDED.expression,
                error_message = EXCLUDED.error_message,
                is_active = EXCLUDED.is_active,
                updated_by = EXCLUDED.updated_by,
                updated_at = CURRENT_TIMESTAMP
        """,
            rule.id, rule.displayName.value, rule.description?.value,
            rule.ruleType, rule.entityType, rule.expression,
            rule.errorMessage, true, rule.metadata.audit.createdBy,
            rule.metadata.audit.updatedBy
        )
        
        // Save parameters
        rule.parameters.forEach { param ->
            jdbcTemplate.update("""
                INSERT INTO ad_rule_parameter (
                    rule_id, name, data_type, mandatory, default_value
                ) VALUES (?, ?, ?, ?, ?)
                ON CONFLICT (rule_id, name) DO UPDATE SET
                    data_type = EXCLUDED.data_type,
                    mandatory = EXCLUDED.mandatory,
                    default_value = EXCLUDED.default_value
            """,
                rule.id, param.name, param.dataType.name,
                param.mandatory, param.defaultValue
            )
        }
        return Unit.right()
    }

    override suspend fun <T : Any> findById(id: UUID, type: Class<T>): Either<MetadataError, T?> = try {
        when (type) {
            ADRule::class.java -> findRuleById(id)
            ADValidationRule::class.java -> findValidationRuleById(id)
            ADStatusLine::class.java -> findStatusLineById(id)
            else -> throw IllegalArgumentException("Unsupported entity type")
        }.map { it as T }
    } catch (e: Exception) {
        logger.error("Failed to find metadata entity", e)
        MetadataError.NotFound("Entity not found: $id").left()
    }

    override suspend fun findRulesByType(entityType: String): Flow<ADRule> = flow {
        val sql = """
            SELECT r.*, rp.name as param_name, rp.data_type as param_type,
                   rp.mandatory as param_mandatory, rp.default_value as param_default
            FROM ad_rule r
            LEFT JOIN ad_rule_parameter rp ON r.id = rp.rule_id
            WHERE r.entity_type = ? AND r.is_active = true
            ORDER BY r.id
        """
        
        jdbcTemplate.query(sql, { rs, _ ->
            ADRule(
                metadata = EntityMetadata(
                    id = rs.getString("id"),
                    audit = AuditInfo(
                        createdBy = rs.getString("created_by"),
                        updatedBy = rs.getString("updated_by")
                    )
                ),
                displayName = DisplayName.create(rs.getString("display_name")).orNull()!!,
                description = rs.getString("description")?.let { 
                    Description.create(it).orNull() 
                },
                ruleType = rs.getString("rule_type"),
                entityType = rs.getString("entity_type"),
                expression = rs.getString("expression"),
                errorMessage = rs.getString("error_message"),
                parameters = buildParameters(rs)
            )
        }, entityType).forEach { emit(it) }
    }

    private fun buildParameters(rs: java.sql.ResultSet): List<RuleParameter> {
        val paramName = rs.getString("param_name") ?: return emptyList()
        return listOf(
            RuleParameter(
                name = paramName,
                dataType = DataType.valueOf(rs.getString("param_type")),
                mandatory = rs.getBoolean("param_mandatory"),
                defaultValue = rs.getString("param_default")
            )
        )
    }

    // Similar implementations for validation rules and status lines...
    // Keeping focused on core rule metadata first for POC
}
