package org.blackerp.domain.table.extension

import arrow.core.Either
import org.blackerp.domain.table.ADTable
import org.blackerp.plugin.Extension
import org.blackerp.plugin.PluginError

interface TableExtensionPoint {
    suspend fun beforeCreate(table: ADTable): Either<PluginError, ADTable>
    suspend fun afterCreate(table: ADTable)
    suspend fun beforeUpdate(table: ADTable): Either<PluginError, ADTable>
    suspend fun afterUpdate(table: ADTable)
    suspend fun beforeDelete(table: ADTable): Either<PluginError, Unit>
    suspend fun afterDelete(table: ADTable)
}
