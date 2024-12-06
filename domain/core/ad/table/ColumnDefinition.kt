package org.blackerp.domain.core.ad.table

data class ColumnDefinition(
    val name: String,
    val dataType: String,
    val length: Int? = null
)
