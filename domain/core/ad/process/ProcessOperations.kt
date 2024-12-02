package org.blackerp.domain.ad.process

import arrow.core.Either
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface ProcessOperations {
    suspend fun save(process: ADProcess): Either<ProcessError, ADProcess>
    suspend fun findById(id: UUID): Either<ProcessError, ADProcess?>
    suspend fun search(query: String, pageSize: Int = 20, page: Int = 0): Flow<ADProcess>
    suspend fun delete(id: UUID): Either<ProcessError, Unit>
    suspend fun execute(
        id: UUID,
        parameters: Map<String, Any>,
        async: Boolean = false
    ): Either<ProcessError, ProcessResult>
    suspend fun schedule(
        id: UUID,
        schedule: ProcessSchedule
    ): Either<ProcessError, ADProcess>
    suspend fun getExecutionHistory(
        id: UUID,
        pageSize: Int = 20,
        page: Int = 0
    ): Flow<ProcessExecution>
    suspend fun validateParameters(
        id: UUID,
        parameters: Map<String, Any>
    ): Either<ProcessError, Map<String, List<String>>>
}

data class ProcessExecution(
    val id: UUID = UUID.randomUUID(),
    val processId: UUID,
    val startTime: java.time.Instant,
    val endTime: java.time.Instant?,
    val status: ExecutionStatus,
    val parameters: Map<String, Any>,
    val result: ProcessResult?,
    val user: String
)

enum class ExecutionStatus {
    QUEUED,
    RUNNING,
    COMPLETED,
    FAILED,
    CANCELLED
}
