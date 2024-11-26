package org.blackerp.domain.query

sealed interface QueryCriteria {
    data class Equals(val field: String, val value: Any) : QueryCriteria
    data class Like(val field: String, val pattern: String) : QueryCriteria
    data class Between(val field: String, val start: Any, val end: Any) : QueryCriteria
    data class In(val field: String, val values: List<Any>) : QueryCriteria
    data class And(val criteria: List<QueryCriteria>) : QueryCriteria
    data class Or(val criteria: List<QueryCriteria>) : QueryCriteria
}
