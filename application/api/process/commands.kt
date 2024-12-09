package org.blackerp.application.api.process

import org.blackerp.domain.core.values.DataType
import org.blackerp.domain.core.ad.workflow.NodeType

data class ProcessCommand(
   val name: String,
   val displayName: String, 
   val description: String?,
   val type: String,
   val parameters: List<ProcessParameter>
)

data class ProcessParameter(
   val name: String,
   val displayName: String,
   val description: String?,
   val dataType: DataType,
   val mandatory: Boolean = false
)

data class WorkflowCommand(
   val name: String,
   val displayName: String,
   val description: String?,
   val nodeType: NodeType,
   val action: String?,
   val transitions: List<TransitionCommand>
)

data class TransitionCommand(
   val targetNode: String,
   val condition: String?
)
