package org.blackerp.infrastructure.persistence.repositories

import org.springframework.stereotype.Repository
import org.springframework.jdbc.core.JdbcTemplate
import org.blackerp.domain.core.ad.process.*
import org.blackerp.domain.core.error.ProcessError
import org.blackerp.domain.core.metadata.*
import org.blackerp.domain.core.values.*
import arrow.core.Either
import arrow.core.right
import arrow.core.left
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.UUID
import org.slf4j.LoggerFactory
import org.springframework.transaction.annotation.Transactional

@Repository
class ProcessRepositoryImpl(
    private val jdbcTemplate: JdbcTemplate
) : ProcessOperations {
    private val logger = LoggerFactory.getLogger(ProcessRepositoryImpl::class.java)

    @Transactional
    override suspend fun save(process: ADProcess): Either<ProcessError, ADProcess> = try {
        logger.debug("Saving process: ${process.id}")
        
        jdbcTemplate.update("""
            INSERT INTO ad_process (
                id, name, display_name, description, 
                process_type, implementation_type, implementation_value
            ) VALUES (?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT (id) DO UPDATE SET
                display_name = EXCLUDED.display_name,
                description = EXCLUDED.description,
                process_type = EXCLUDED.process_type,
                implementation_type = EXCLUDED.implementation_type,
                implementation_value = EXCLUDED.implementation_value
        """,
            process.id,
            process.displayName.value,
            process.displayName.value,
            process.description?.value,
            process.type.name,
            getImplementationType(process.implementation),
            getImplementationValue(process.implementation)
        )

        // Handle parameters
        saveParameters(process.id, process.parameters)

        process.right()
    } catch (e: Exception) {
        logger.error("Failed to save process: ${process.id}", e)
        ProcessError.ValidationFailed(e.message ?: "Save failed").left()
    }

    override suspend fun findById(id: UUID): Either<ProcessError, ADProcess?> = try {
        val process = jdbcTemplate.query("""
            SELECT p.*, 
                   pp.id as param_id, 
                   pp.name as param_name,
                   pp.display_name as param_display_name,
                   pp.description as param_description,
                   pp.parameter_type,
                   pp.is_mandatory,
                   pp.validation_rule
            FROM ad_process p
            LEFT JOIN ad_process_parameter pp ON p.id = pp.process_id
            WHERE p.id = ?
        """, { rs, _ ->
            // Map result set to ADProcess
            val parameters = mutableListOf<ProcessParameter>()
            
            ADProcess(
                metadata = EntityMetadata(
                    id = rs.getString("id"),
                    audit = AuditInfo(
                        createdBy = rs.getString("created_by"),
                        updatedBy = rs.getString("updated_by")
                    )
                ),
                displayName = DisplayName.create(rs.getString("display_name"))
                    .getOrNull() ?: throw IllegalStateException("Invalid display name"),
                description = rs.getString("description")?.let { desc ->
                    Description.create(desc).getOrNull()
                },
                type = ProcessType.valueOf(rs.getString("process_type")),
                parameters = parameters,
                implementation = mapImplementation(
                    rs.getString("implementation_type"),
                    rs.getString("implementation_value")
                ),
                schedule = null // TODO: Add schedule mapping
            )
        }, id).firstOrNull()

        process.right()
    } catch (e: Exception) {
        logger.error("Failed to find process: $id", e)
        ProcessError.ValidationFailed(e.message ?: "Find failed").left()
    }

    private fun saveParameters(processId: String, parameters: List<ProcessParameter>) {
        // First delete existing parameters
        jdbcTemplate.update("DELETE FROM ad_process_parameter WHERE process_id = ?", processId)

        // Then insert new parameters
        parameters.forEach { param ->
            jdbcTemplate.update("""
                INSERT INTO ad_process_parameter (
                    id, process_id, name, display_name, description,
                    parameter_type, is_mandatory, validation_rule
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """,
                param.id,
                processId,
                param.name,
                param.displayName,
                param.description,
                param.parameterType.name,
                param.isMandatory,
                param.validationRule
            )
        }
    }

    private fun getImplementationType(implementation: ProcessImplementation): String =
        when (implementation) {
            is ProcessImplementation.JavaClass -> "JAVA"
            is ProcessImplementation.DatabaseFunction -> "DB_FUNCTION"
            is ProcessImplementation.Script -> "SCRIPT"
        }

    private fun getImplementationValue(implementation: ProcessImplementation): String =
        when (implementation) {
            is ProcessImplementation.JavaClass -> implementation.className
            is ProcessImplementation.DatabaseFunction -> implementation.functionName
            is ProcessImplementation.Script -> implementation.code
        }

    private fun mapImplementation(type: String, value: String): ProcessImplementation =
        when (type) {
            "JAVA" -> ProcessImplementation.JavaClass(value)
            "DB_FUNCTION" -> ProcessImplementation.DatabaseFunction(value)
            "SCRIPT" -> ProcessImplementation.Script("kotlin", value)
            else -> throw IllegalArgumentException("Unknown implementation type: $type")
        }
}
