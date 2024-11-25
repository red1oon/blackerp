
package org.blackerp.domain.table.behavior

import org.blackerp.domain.table.definition.TableDefinition
import arrow.core.Either
import org.blackerp.domain.DomainEntity 
import org.blackerp.domain.table.TableError

interface TableBehavior : DomainEntity {
    fun beforeSave(table: TableDefinition): Either<TableError, TableDefinition>
    fun afterSave(table: TableDefinition)
    fun beforeDelete(table: TableDefinition): Either<TableError, Unit>
    fun afterDelete(table: TableDefinition)
}
