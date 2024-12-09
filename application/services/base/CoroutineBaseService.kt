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
