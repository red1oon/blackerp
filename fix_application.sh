#!/bin/bash

cat > application/api/process/ApiCommands.kt << 'END'
package org.blackerp.application.api.process

import org.blackerp.domain.core.ad.process.*
import java.util.UUID

data class ApiCreateProcessCommand(
   val displayName: String,
   val description: String?,
   val type: ProcessType,
   val parameters: List<ApiParameter>,
   val implementation: ProcessImplementation,  
   val schedule: ProcessSchedule?
)

data class ApiParameter(
   val name: String,
   val displayName: String,
   val description: String?, 
   val dataType: DataType,
   val mandatory: Boolean = false,
   val defaultValue: String? = null,
   val validation: ParameterValidation? = null
) {
   fun toADParameter() = ADParameter(
       id = UUID.randomUUID(),
       name = name,
       displayName = displayName,
       description = description,
       dataType = dataType,
       mandatory = mandatory,
       defaultValue = defaultValue,
       validation = validation
   )
}
END

cat > application/usecases/process/ProcessExtensions.kt << 'END' 
package org.blackerp.application.usecases.process

import org.blackerp.domain.core.ad.process.*
import org.blackerp.domain.core.metadata.*
import org.blackerp.domain.core.values.*
import org.blackerp.application.api.process.ApiCreateProcessCommand
import arrow.core.Either
import arrow.core.right
import java.util.UUID

fun ApiCreateProcessCommand.toProcess(): Either<ProcessError, ADProcess> =
   ADProcess(
       metadata = EntityMetadata(
           id = UUID.randomUUID().toString(),
           audit = AuditInfo(createdBy = "system", updatedBy = "system")
       ),
       displayName = DisplayName.create(displayName).getOrNull() 
           ?: throw IllegalArgumentException("Invalid display name"),
       description = description?.let { desc ->
           Description.create(desc).getOrNull()
               ?: throw IllegalArgumentException("Invalid description")
       },
       type = type,
       parameters = parameters.map { it.toADParameter() },
       implementation = implementation,
       schedule = schedule
   ).right()
END

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
   private val processExecutor: ProcessExecutor,
   private val errorMapper: DomainProcessErrorMapper
) : ProcessOperations, CoroutineBaseService() {

   override suspend fun save(process: ADProcess): Either<ProcessError, ADProcess> =
       processRepository.save(process)
           .mapLeft { errorMapper.mapToDomainError(it) }

   override suspend fun findById(id: java.util.UUID): Either<ProcessError, ADProcess?> =
       processRepository.findById(id)
           .mapLeft { errorMapper.mapToDomainError(it) }

   override suspend fun search(query: String, pageSize: Int, page: Int): Flow<ADProcess> =
       processRepository.search(query, pageSize, page)

   override suspend fun delete(id: java.util.UUID): Either<ProcessError, Unit> =
       processRepository.delete(id) 
           .mapLeft { errorMapper.mapToDomainError(it) }

   override suspend fun execute(
       id: java.util.UUID,
       parameters: Map<String, Any>,
       async: Boolean
   ): Either<ProcessError, ProcessResult> =
       processExecutor.execute(id, parameters, async)
           .mapLeft { errorMapper.mapToDomainError(it) }

   override suspend fun schedule(id: java.util.UUID, schedule: ProcessSchedule): Either<ProcessError, ADProcess> =
       withTransaction {
           findById(id).flatMap { process ->
               process?.copy(schedule = schedule)?.let { updated ->
                   save(updated)
               } ?: ProcessError.NotFound(id).left()
           }
       }

   override suspend fun getExecutionHistory(id: java.util.UUID, pageSize: Int, page: Int): Flow<ProcessExecution> =
       processRepository.getExecutionHistory(id, pageSize, page)

   override suspend fun validateParameters(id: java.util.UUID, parameters: Map<String, Any>): Either<ProcessError, Map<String, List<String>>> =
       findById(id).map { process ->
           validateParameterTypes(process?.parameters ?: emptyList(), parameters)
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

cat > application/services/error/DomainProcessErrorMapper.kt << 'END'
package org.blackerp.application.services.error

import org.springframework.stereotype.Component
import org.blackerp.domain.core.ad.process.ProcessError

@Component
class DomainProcessErrorMapper {
   fun mapToDomainError(error: ProcessError): ProcessError = error
}
END

chmod +x fix_application.sh
