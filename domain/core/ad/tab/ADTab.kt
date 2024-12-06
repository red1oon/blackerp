package org.blackerp.domain.core.ad.tab

import arrow.core.Either
import arrow.core.right
import arrow.core.left
import org.blackerp.domain.core.DomainEntity
import org.blackerp.domain.core.shared.EntityMetadata
import org.blackerp.domain.core.ad.base.ADObject
import org.blackerp.domain.core.values.DisplayName
import org.blackerp.domain.core.values.Description
import org.blackerp.domain.core.values.ColumnName
import org.blackerp.domain.core.shared.ValidationError
import org.blackerp.domain.core.ad.table.ADTable
import org.blackerp.domain.core.ad.table.ColumnDefinition
import java.util.UUID

data class CreateTabParams(
    val metadata: EntityMetadata,
    val displayName: DisplayName,
    val description: Description?,
    val table: ADTable,
    val queryColumns: List<ColumnName>,
    val displayColumns: List<ColumnName>,
    val orderBy: List<OrderBySpec>
)

data class ADTab(
    override val metadata: EntityMetadata,
    private val uuid: UUID = UUID.randomUUID(),
    override val displayName: DisplayName,
    override val description: Description?,
    val table: ADTable,
    val queryColumns: List<ColumnName>,
    val displayColumns: List<ColumnName>,
    val orderBy: List<OrderBySpec>
) : ADObject {
    override val id: String 
        get() = uuid.toString()

    companion object {
        fun create(params: CreateTabParams): Either<TabError, ADTab> {
            val errors = mutableListOf<ValidationError>()

            // Validate query columns exist in table
            val invalidQueryColumns = params.queryColumns.filter { queryColumn: ColumnName ->
                params.table.columns.none { column: ColumnDefinition -> 
                    column.name == queryColumn.value 
                }
            }
            if (invalidQueryColumns.isNotEmpty()) {
                errors.add(ValidationError.InvalidValue(
                    "Query columns not found in table: ${invalidQueryColumns.joinToString { column -> column.value }}"
                ))
            }

            // Validate display columns exist in table
            val invalidDisplayColumns = params.displayColumns.filter { displayColumn: ColumnName ->
                params.table.columns.none { column: ColumnDefinition -> 
                    column.name == displayColumn.value 
                }
            }
            if (invalidDisplayColumns.isNotEmpty()) {
                errors.add(ValidationError.InvalidValue(
                    "Display columns not found in table: ${invalidDisplayColumns.joinToString { column -> column.value }}"
                ))
            }

            // Validate order by columns exist in table
            val invalidOrderColumns = params.orderBy.filter { orderSpec: OrderBySpec ->
                params.table.columns.none { column: ColumnDefinition -> 
                    column.name == orderSpec.column.value 
                }
            }
            if (invalidOrderColumns.isNotEmpty()) {
                errors.add(ValidationError.InvalidValue(
                    "Order by columns not found in table: ${invalidOrderColumns.joinToString { spec -> spec.column.value }}"
                ))
            }

            return if (errors.isEmpty()) {
                ADTab(
                    metadata = params.metadata,
                    displayName = params.displayName,
                    description = params.description,
                    table = params.table,
                    queryColumns = params.queryColumns,
                    displayColumns = params.displayColumns,
                    orderBy = params.orderBy
                ).right()
            } else {
                TabError.ValidationFailed(errors).left()
            }
        }
    }
}
