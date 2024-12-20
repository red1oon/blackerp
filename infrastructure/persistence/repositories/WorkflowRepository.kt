package org.blackerp.infrastructure.persistence.repositories

import org.springframework.stereotype.Repository
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.transaction.annotation.Transactional
import org.blackerp.domain.core.ad.workflow.*
import org.blackerp.domain.core.error.TableError
import arrow.core.Either
import arrow.core.right
import arrow.core.left
import java.util.UUID
import org.slf4j.LoggerFactory

@Repository
class WorkflowRepository(
    private val jdbcTemplate: JdbcTemplate
) : WorkflowOperations {
    private val logger = LoggerFactory.getLogger(WorkflowRepository::class.java)

    @Transactional
    override suspend fun save(node: WorkflowNode): Either<WorkflowError, WorkflowNode> = try {
        logger.debug("Saving workflow node: ${node.id}")
        jdbcTemplate.update("""
            INSERT INTO workflow_node (
                id, type, display_name, description,
                action_type, action_data
            ) VALUES (?, ?, ?, ?, ?, ?)
            ON CONFLICT (id) DO UPDATE SET
                type = EXCLUDED.type,
                display_name = EXCLUDED.display_name,
                description = EXCLUDED.description,
                action_type = EXCLUDED.action_type,
                action_data = EXCLUDED.action_data
            """,
            node.id,
            node.type.name,
            node.displayName.value,
            node.description?.value,
            node.action?.javaClass?.simpleName,
            node.action?.toString()
        )

        handleTransitions(node)
        node.right()
    } catch (e: Exception) {
        logger.error("Failed to save node: ${node.id}", e)
        WorkflowError.ValidationFailed("Save failed: ${e.message}").left()
    }

    override suspend fun findById(id: UUID): Either<WorkflowError, WorkflowNode?> =
        null.right()

    override suspend fun delete(id: UUID): Either<WorkflowError, Unit> =
        Unit.right()

    override suspend fun validateWorkflow(nodes: List<WorkflowNode>): Either<WorkflowError, Unit> =
        Unit.right()

    private fun handleTransitions(node: WorkflowNode) {
        jdbcTemplate.update(
            "DELETE FROM workflow_transition WHERE source_node_id = ?",
            node.id
        )

        node.transitions.forEach { transition ->
            jdbcTemplate.update("""
                INSERT INTO workflow_transition (
                    id, source_node_id, target_node_id,
                    condition_expression
                ) VALUES (?, ?, ?, ?)
                """,
                transition.id,
                node.id,
                transition.targetNode,
                transition.condition?.expression
            )
        }
    }
}
