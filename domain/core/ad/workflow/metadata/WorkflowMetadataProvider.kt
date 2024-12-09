package org.blackerp.domain.core.ad.workflow.metadata

import arrow.core.Either
import org.blackerp.domain.core.error.WorkflowError
import java.util.UUID

/**
 * Provides access to AD workflow metadata.
 * Follows iDempiere pattern where workflow behavior is driven by metadata,
 * not code changes.
 */
interface WorkflowMetadataProvider {
    suspend fun getWorkflowMetadata(documentId: UUID): Either<WorkflowError, WorkflowMetadata>
    suspend fun getNodeMetadata(nodeId: UUID): Either<WorkflowError, NodeMetadata>
    suspend fun getTransitionMetadata(
        fromNode: UUID,
        toNode: UUID
    ): Either<WorkflowError, TransitionMetadata>
}
