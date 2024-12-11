package org.blackerp.application.services.core.base

/**
 * Generic search criteria for domain entities.
 * Extendable by specific domain services for custom search parameters.
 */
interface SearchCriteria {
    val pageSize: Int
    val page: Int
}

data class BaseSearchCriteria(
    override val pageSize: Int = 20,
    override val page: Int = 0
) : SearchCriteria
