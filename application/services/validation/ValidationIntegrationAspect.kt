package org.blackerp.application.services.validation

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component
import arrow.core.Either
import arrow.core.flatMap
import org.slf4j.LoggerFactory

@Aspect
@Component
class ValidationIntegrationAspect(
    private val validatorProvider: ServiceValidatorProvider
) {
    private val logger = LoggerFactory.getLogger(ValidationIntegrationAspect::class.java)

    @Around("@annotation(ValidateMetadata)")
    suspend fun validateMetadata(joinPoint: ProceedingJoinPoint): Any? {
        val entity = joinPoint.args.firstOrNull()
        val entityType = joinPoint.target::class.java.simpleName

        return when {
            entity == null -> joinPoint.proceed()
            else -> validatorProvider.validateEntity(entity, entityType)
                .flatMap { validatedEntity -> 
                    @Suppress("UNCHECKED_CAST")
                    joinPoint.proceed(arrayOf(validatedEntity)) as Either<*, *>
                }
        }
    }
}
