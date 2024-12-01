package org.blackerp.domain.table

import arrow.core.Either
import arrow.core.right
import org.blackerp.domain.DomainEntity
import org.blackerp.domain.EntityMetadata
import org.blackerp.domain.values.TableName
import org.blackerp.domain.values.DisplayName
import org.blackerp.domain.values.Description
import org.blackerp.domain.values.AccessLevel
import org.blackerp.domain.table.ColumnDefinition

data class ADTable(
    override val metadata: EntityMetadata,
    val name: TableName,
    val displayName: DisplayName,
    val description: Description?,
    val accessLevel: AccessLevel,
    val columns: List<ColumnDefinition> // Added columns property
) : DomainEntity {
    companion object {
        fun create(params: CreateTableParams): Either<TableError, ADTable> =
            ADTable(
                metadata = params.metadata,
                name = params.name,
                displayName = params.displayName,
                description = params.description,
                accessLevel = params.accessLevel,
                columns = params.columns // Initialize columns
            ).right()
    }
}

data class CreateTableParams(
    val metadata: EntityMetadata,
    val name: TableName,
    val displayName: DisplayName,
    val description: Description?,
    val accessLevel: AccessLevel,
    val columns: List<ColumnDefinition>  
)
