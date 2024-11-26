package org.blackerp.infrastructure.cache

import arrow.core.Either
import arrow.core.right
import org.blackerp.domain.error.DomainError
import java.time.Duration
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap

class InMemoryCacheService : CacheService {
    private data class CacheEntry<T>(
        val value: T,
        val expiry: Instant?
    )

    private val cache = ConcurrentHashMap<String, CacheEntry<Any>>()

    @Suppress("UNCHECKED_CAST")
    override suspend fun <T> get(key: String): Either<DomainError, T?> =
        cache[key]?.let { entry ->
            if (entry.expiry == null || entry.expiry > Instant.now()) {
                (entry.value as T).right()
            } else {
                cache.remove(key)
                null.right()
            }
        } ?: null.right()

    override suspend fun <T> set(key: String, value: T, ttl: Duration?): Either<DomainError, Unit> {
        val expiry = ttl?.let { Instant.now().plus(it) }
        cache[key] = CacheEntry(value as Any, expiry)
        return Unit.right()
    }

    override suspend fun delete(key: String): Either<DomainError, Unit> {
        cache.remove(key)
        return Unit.right()
    }

    override suspend fun clear(): Either<DomainError, Unit> {
        cache.clear()
        return Unit.right()
    }
}
