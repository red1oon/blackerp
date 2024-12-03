package org.blackerp.domain.core.ad.workflow

sealed interface NodeAction {
    data class Process(val processId: UUID) : NodeAction
    data class UserTask(val roleId: UUID) : NodeAction
    data class Notification(val template: String) : NodeAction
}
