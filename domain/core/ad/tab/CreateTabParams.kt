
package org.blackerp.domain.ad.tab

import org.blackerp.domain.EntityMetadata
import org.blackerp.domain.table.ADTable
import org.blackerp.domain.values.ColumnName
import org.blackerp.domain.values.DisplayName
import org.blackerp.domain.values.Description
import org.blackerp.domain.ad.tab.value.TabName

data class CreateTabParams(
    val metadata: EntityMetadata,
    val name: TabName,
    val displayName: DisplayName,
    val description: Description?,
    val table: ADTable,
    val queryColumns: List<ColumnName>,
    val displayColumns: List<ColumnName>,
    val orderBy: List<OrderBySpec>
)
