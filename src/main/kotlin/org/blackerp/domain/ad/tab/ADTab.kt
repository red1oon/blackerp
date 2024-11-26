
package org.blackerp.domain.ad.tab

import arrow.core.Either
import arrow.core.right
import arrow.core.left
import org.blackerp.domain.DomainEntity
import org.blackerp.domain.EntityMetadata
import org.blackerp.domain.ad.ADObject
import org.blackerp.domain.ad.tab.value.TabName
import org.blackerp.domain.values.*
import org.blackerp.domain.table.ADTable
import org.blackerp.shared.ValidationError

data class ADTab(
    override val metadata: EntityMetadata,
    val name: TabName,
    override val displayName: DisplayName,
    override val description: Description?,
    val table: ADTable,
    val queryColumns: List<ColumnName>,
    val displayColumns: List<ColumnName>,
    val orderBy: List<OrderBySpec>
) : ADObject {
    companion object {
        fun create(params: CreateTabParams): Either<TabError, ADTab> {
            val errors = mutableListOf<ValidationError>()
            
            // Validate query columns exist in table
            val invalidQueryColumns = params.queryColumns.filter { queryCol ->
                !params.table.columns.any { it.name == queryCol }
            }
            if (invalidQueryColumns.isNotEmpty()) {
                errors.add(ValidationError.InvalidValue(
                    "Query columns not found in table: ${invalidQueryColumns.joinToString { it.value }}"
                ))
            }
            
            // Validate display columns exist in table
            val invalidDisplayColumns = params.displayColumns.filter { displayCol ->
                !params.table.columns.any { it.name == displayCol }
            }
            if (invalidDisplayColumns.isNotEmpty()) {
                errors.add(ValidationError.InvalidValue(
                    "Display columns not found in table: ${invalidDisplayColumns.joinToString { it.value }}"
                ))
            }

            // Validate order by columns exist in table
            val invalidOrderColumns = params.orderBy.filter { orderSpec ->
                !params.table.columns.any { it.name == orderSpec.column }
            }
            if (invalidOrderColumns.isNotEmpty()) {
                errors.add(ValidationError.InvalidValue(
                    "Order by columns not found in table: ${invalidOrderColumns.joinToString { it.column.value }}"
                ))
            }

            if (errors.isNotEmpty()) {
                return TabError.ValidationFailed(errors).left()
            }

            return ADTab(
                metadata = params.metadata,
                name = params.name,
                displayName = params.displayName,
                description = params.description,
                table = params.table,
                queryColumns = params.queryColumns,
                displayColumns = params.displayColumns,
                orderBy = params.orderBy
            ).right()
        }
    }
}
