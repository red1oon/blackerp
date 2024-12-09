// domain/core/process/ProcessRepository.kt
package org.blackerp.domain.core.ad.process

import arrow.core.Either
import kotlinx.coroutines.flow.Flow
import org.blackerp.domain.core.error.ProcessError
import java.util.UUID 

interface ProcessRepository {
    suspend fun save(process: ADProcess): Either<ProcessError, ADProcess>
    suspend fun findById(id: UUID): Either<ProcessError, ADProcess?>
    suspend fun delete(id: UUID): Either<ProcessError, Unit>
    suspend fun search(query: String, pageSize: Int = 20, page: Int = 0): Flow<ADProcess>
    suspend fun getExecutionHistory(id: UUID, pageSize: Int = 20, page: Int = 0): Flow<ProcessExecution>
}

interface ProcessExecutor {
    suspend fun execute(
        processId: UUID, 
        parameters: Map<String, Any>,
        async: Boolean = false
    ): Either<ProcessError, ProcessResult>
}
