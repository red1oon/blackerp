package org.blackerp.application.api.process

import org.blackerp.domain.core.ad.process.*
import org.blackerp.domain.core.values.*

data class ApiCreateProcessCommand(
    val displayName: String,
    val description: String?,
    val type: ProcessType,
    val parameters: List<ProcessParameter>,
    val implementation: ProcessImplementation,
    val schedule: ProcessSchedule?
)
