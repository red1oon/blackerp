package org.blackerp.application.services.interfaces

import java.util.UUID
import arrow.core.Either
import org.blackerp.domain.core.error.ProcessError

data class ProcessResult(val success: Boolean, val message: String)

interface ProcessExecutor {
    suspend fun execute(processId: UUID, params: Map<String, Any>, async: Boolean = false): Either<ProcessError, ProcessResult>
}

interface ProcessRepository {
    suspend fun save(process: Process): Either<ProcessError, Process>  
    suspend fun findById(id: UUID): Either<ProcessError, Process?>
}
