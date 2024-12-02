package org.blackerp.domain.ad.process

import arrow.core.Either
import java.util.UUID

interface ProcessOperations {
    suspend fun save(process: ADProcess): Either<ProcessError, ADProcess>
    suspend fun findById(id: UUID): Either<ProcessError, ADProcess?>
    suspend fun delete(id: UUID): Either<ProcessError, Unit>
    suspend fun execute(id: UUID, parameters: Map<String, Any>): Either<ProcessError, ProcessResult>
    suspend fun schedule(id: UUID, schedule: ProcessSchedule): Either<ProcessError, ADProcess>
}

data class ProcessResult(
    val success: Boolean,
    val message: String?,
    val data: Map<String, Any>? = null
)
