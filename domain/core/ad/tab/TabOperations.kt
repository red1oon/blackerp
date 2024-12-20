package org.blackerp.domain.core.ad.tab

import arrow.core.Either
import org.blackerp.domain.core.values.TableName
import java.util.UUID

interface TabOperations {
    suspend fun save(tab: ADTab): Either<TabError, ADTab>
    suspend fun findById(id: UUID): Either<TabError, ADTab?>
    suspend fun findByTable(tableName: TableName): Either<TabError, List<ADTab>>
    suspend fun delete(id: UUID): Either<TabError, Unit>
}
