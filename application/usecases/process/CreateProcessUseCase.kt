package org.blackerp.application.usecases.process

import org.springframework.stereotype.Service
import org.blackerp.domain.core.ad.process.*
import org.blackerp.application.api.process.ProcessRequest
import arrow.core.Either
import arrow.core.flatMap
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers

@Service
class CreateProcessUseCase(private val processOperations: ProcessOperations) {
   suspend fun execute(request: ProcessRequest): Either<ProcessError, ADProcess> = 
       withContext(Dispatchers.IO) {
           request.toDomain().flatMap { process ->
               processOperations.save(process)
           }
       }
}
