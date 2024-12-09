package org.blackerp.domain.core.ad.workflow.metadata

import org.blackerp.domain.core.shared.EntityMetadata
import org.blackerp.domain.core.ad.base.ADObject
import org.blackerp.domain.core.values.AccessLevel
import java.util.UUID

/**
 * AD-driven workflow metadata.
 * Following iDempiere's AD design principle where all workflow definitions
 * are stored as metadata, isolating business process definitions from code.
 */
interface WorkflowMetadata : ADObject {
    val name: String
    val accessLevel: AccessLevel
    val nodes: List<NodeMetadata>
    val transitions: List<TransitionMetadata>
}

data class NodeMetadata(
    val id: UUID,
    val name: String,
    val actionType: String,
    val actionMetadata: Map<String, Any>,
    val conditions: List<ConditionMetadata>
)

data class TransitionMetadata(
    val fromNodeId: UUID,
    val toNodeId: UUID,
    val conditions: List<ConditionMetadata>
)

data class ConditionMetadata(
    val type: String,
    val expression: String,
    val parameters: Map<String, Any>
)
