package org.blackerp.domain.query

import arrow.core.Either
import org.blackerp.domain.error.DomainError

data class QueryResult<T>(
    val items: List<T>,
    val total: Long,
    val page: Int,
    val pageSize: Int
)

interface QueryExecutor<T> {
    suspend fun execute(criteria: QueryCriteria, page: Int, pageSize: Int): Either<DomainError, QueryResult<T>>
}
