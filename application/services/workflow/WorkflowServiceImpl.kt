// application/services/workflow/WorkflowServiceImpl.kt
package org.blackerp.application.services.workflow

import org.springframework.stereotype.Service
import org.blackerp.domain.core.ad.workflow.*
import org.blackerp.domain.events.WorkflowEvent
import org.blackerp.domain.events.EventMetadata 
import org.blackerp.infrastructure.events.publishers.DomainEventPublisher 
import org.blackerp.domain.core.shared.ValidationError
import arrow.core.*
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import org.slf4j.LoggerFactory

@Service
class WorkflowServiceImpl(
    private val workflowOperations: WorkflowOperations,
    private val eventPublisher: DomainEventPublisher
) : WorkflowService {
    private val logger = LoggerFactory.getLogger(WorkflowServiceImpl::class.java)

    override suspend fun createWorkflow(command: CreateWorkflowCommand): Either<WorkflowError, WorkflowNode> {
        logger.debug("Creating workflow node: {}", command.displayName)
        return command.toNode().flatMap { node ->
            workflowOperations.save(node).also { result ->
                result.fold(
                    { error -> logger.error("Failed to create node: {}", error.message) },
                    { savedNode ->
                        eventPublisher.publish(
                            WorkflowEvent.NodeCreated(
                                metadata = EventMetadata(user = "system"),
                                node = savedNode
                            )
                        )
                    }
                )
            }
        }
    }

    override suspend fun updateWorkflow(id: UUID, command: UpdateWorkflowCommand): Either<WorkflowError, WorkflowNode> {
        logger.debug("Updating workflow node: {}", id)
        return workflowOperations.findById(id).flatMap { existing ->
            existing?.let { node ->
                val updated = node.copy(
                    displayName = command.displayName ?: node.displayName,
                    description = command.description ?: node.description
                )
                workflowOperations.save(updated).also { result ->
                    result.fold(
                        { error -> logger.error("Failed to update node: {}", error.message) },
                        { updatedNode ->
                            eventPublisher.publish(
                                WorkflowEvent.NodeUpdated(
                                    metadata = EventMetadata(user = "system"),
                                    nodeId = UUID.fromString(updatedNode.id),
                                    changes = mapOf() // Add actual changes tracking
                                )
                            )
                        }
                    )
                }
            } ?: WorkflowError.NotFound(id).left()
        }
    }

    override suspend fun validateWorkflow(id: UUID): Either<WorkflowError, ValidationResult> {
        logger.debug("Validating workflow: {}", id)
        return workflowOperations.findById(id).flatMap { node ->
            node?.let { 
                workflowOperations.validateWorkflow(listOf(it)).map { 
                    ValidationResult(valid = true) 
                }
            } ?: WorkflowError.NotFound(id).left()
        }
    }

    override suspend fun executeWorkflow(id: UUID, context: WorkflowContext): Either<WorkflowError, WorkflowResult> {
        logger.debug("Executing workflow: {} for document: {}", id, context.document.id)
        return workflowOperations.findById(id).flatMap { node ->
            node?.let { 
                // Implement actual workflow execution logic
                WorkflowResult(
                    completed = true,
                    currentNode = UUID.fromString(node.id),
                    nextNodes = emptyList(),
                    attributes = context.attributes
                ).right()
            } ?: WorkflowError.NotFound(id).left()
        }
    }
}