#!/bin/bash

# Create CoroutineBaseService.kt
mkdir -p application/services/base
cat > application/services/base/CoroutineBaseService.kt << 'END'
package org.blackerp.application.services.base

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import arrow.core.Either
import arrow.core.left
import org.blackerp.domain.core.error.DomainError
import org.blackerp.domain.core.error.UnexpectedError

abstract class CoroutineBaseService {
   protected suspend fun <T> withTransaction(
       block: suspend () -> Either<DomainError, T>
   ): Either<DomainError, T> = withContext(Dispatchers.IO) {
       try {
           block()
       } catch (e: Exception) {
           UnexpectedError(e.message ?: "Unknown error").left()
       }
   }
}
END

# Update CreateWorkflowUseCase.kt
cat > application/usecases/workflow/CreateWorkflowUseCase.kt << 'END'
package org.blackerp.application.usecases.workflow

import org.springframework.stereotype.Service
import org.blackerp.domain.core.ad.workflow.*
import org.blackerp.application.services.base.CoroutineBaseService
import arrow.core.Either

@Service
class CreateWorkflowUseCase(
   private val workflowOperations: WorkflowOperations
) : CoroutineBaseService() {

   suspend fun execute(command: CreateWorkflowCommand): Either<WorkflowError, WorkflowNode> = 
       withTransaction {
           command.toNode().flatMap { node ->
               workflowOperations.save(node)
           }
       }
}
END

# Update CreateProcessUseCase.kt  
cat > application/usecases/process/CreateProcessUseCase.kt << 'END'
package org.blackerp.application.usecases.process

import org.springframework.stereotype.Service
import org.blackerp.domain.core.ad.process.*
import org.blackerp.application.services.base.CoroutineBaseService
import arrow.core.Either

@Service
class CreateProcessUseCase(
   private val processOperations: ProcessOperations
) : CoroutineBaseService() {

   suspend fun execute(command: CreateProcessCommand): Either<ProcessError, ADProcess> =
       withTransaction {
           command.toProcess().flatMap { process ->
               processOperations.save(process)
           }
       }
}
END

# Update ProcessService.kt
cat > application/services/ProcessService.kt << 'END' 
package org.blackerp.application.services

import org.springframework.stereotype.Service
import org.blackerp.domain.core.ad.process.*
import org.blackerp.application.services.base.CoroutineBaseService
import arrow.core.*
import kotlinx.coroutines.flow.*

@Service
class ProcessService(
   private val processRepository: ProcessRepository,
   private val processExecutor: ProcessExecutor
) : ProcessOperations, CoroutineBaseService() {

   override suspend fun save(process: ADProcess): Either<ProcessError, ADProcess> =
       withTransaction { processRepository.save(process) }

   override suspend fun findById(id: java.util.UUID): Either<ProcessError, ADProcess?> =
       withTransaction { processRepository.findById(id) }

   override suspend fun search(query: String, pageSize: Int, page: Int): Flow<ADProcess> =
       flow { emitAll(processRepository.search(query, pageSize, page)) }

   override suspend fun delete(id: java.util.UUID): Either<ProcessError, Unit> =
       withTransaction { processRepository.delete(id) }

   override suspend fun execute(
       id: java.util.UUID,
       parameters: Map<String, Any>,
       async: Boolean
   ): Either<ProcessError, ProcessResult> =
       withTransaction { processExecutor.execute(id, parameters, async) }

   override suspend fun schedule(id: java.util.UUID, schedule: ProcessSchedule): Either<ProcessError, ADProcess> =
       withTransaction {
           findById(id).flatMap { process ->
               process?.copy(schedule = schedule)?.let { updated ->
                   save(updated)
               } ?: ProcessError.NotFound(id).left()
           }
       }

   override suspend fun getExecutionHistory(id: java.util.UUID, pageSize: Int, page: Int): Flow<ProcessExecution> =
       flow { emitAll(processRepository.getExecutionHistory(id, pageSize, page)) }

   override suspend fun validateParameters(id: java.util.UUID, parameters: Map<String, Any>): Either<ProcessError, Map<String, List<String>>> =
       withTransaction {
           findById(id).map { process ->
               validateParameterTypes(process?.parameters ?: emptyList(), parameters)
           }
       }

   private fun validateParameterTypes(processParams: List<ADParameter>, params: Map<String, Any>): Map<String, List<String>> {
       val errors = mutableMapOf<String, List<String>>()
       processParams.forEach { param ->
           if (param.mandatory && !params.containsKey(param.name)) {
               errors[param.name] = listOf("Required parameter missing")
           }
       }
       return errors
   }
}
END

chmod +x fix_coroutines.sh
