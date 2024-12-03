
package org.blackerp.domain.core.ad.tab

import org.blackerp.domain.core.EntityMetadata
import org.blackerp.domain.core.ad.table.ADTable
import org.blackerp.domain.core.values.ColumnName
import org.blackerp.domain.core.values.DisplayName
import org.blackerp.domain.core.values.Description
import org.blackerp.domain.core.ad.tab.value.TabName

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
