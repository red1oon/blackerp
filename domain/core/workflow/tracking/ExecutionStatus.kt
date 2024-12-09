package org.blackerp.domain.core.workflow.tracking

enum class ExecutionStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED,
    FAILED,
    CANCELLED;
    
    fun isTerminal() = this in listOf(COMPLETED, FAILED, CANCELLED)
}
