package org.blackerp.infrastructure.cache

import arrow.core.Either
import org.blackerp.domain.error.DomainError
import java.time.Duration

interface CacheService {
    suspend fun <T> get(key: String): Either<DomainError, T?>
    suspend fun <T> set(key: String, value: T, ttl: Duration? = null): Either<DomainError, Unit>
    suspend fun delete(key: String): Either<DomainError, Unit>
    suspend fun clear(): Either<DomainError, Unit>
}
