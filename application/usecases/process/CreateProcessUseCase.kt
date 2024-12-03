package org.blackerp.application.usecases.process

import org.blackerp.domain.core.ad.process.*
import org.springframework.stereotype.Service
import arrow.core.Either

@Service
class CreateProcessUseCase(
    private val processOperations: ProcessOperations
) {
    suspend fun execute(command: CreateProcessCommand): Either<ProcessError, ADProcess> =
        command.toProcess().flatMap { process ->
            processOperations.save(process)
        }
}

data class CreateProcessCommand(
    val displayName: String,
    val description: String?,
    val parameters: List<CreateParameterCommand>,
    val implementation: ProcessImplementation,
    val schedule: ProcessSchedule?
)

data class CreateParameterCommand(
    val name: String,
    val displayName: String,
    val description: String?,
    val dataType: String,
    val mandatory: Boolean,
    val defaultValue: String?
)
