package org.blackerp.application.api.dto.requests

import org.blackerp.domain.core.ad.process.*
import java.util.UUID

data class CreateProcessRequest(
   val displayName: String,
   val type: String,
   val parameters: List<ProcessParameterRequest>
)

data class ProcessParameterRequest(
   val name: String,
   val displayName: String,
   val description: String?,
   val mandatory: Boolean = false
)
