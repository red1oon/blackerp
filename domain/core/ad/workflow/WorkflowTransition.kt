package org.blackerp.domain.core.ad.workflow

import java.util.UUID

data class WorkflowTransition(
    val id: UUID,
    val sourceNode: UUID,
    val targetNode: UUID,
    val condition: TransitionCondition?
)

data class TransitionCondition(
    val expression: String,
    val description: String
)
