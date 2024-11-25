package org.blackerp.shared

import org.blackerp.application.table.CreateColumnCommand
import org.blackerp.application.table.CreateTableCommand
import org.blackerp.api.dto.CreateTableRequest
import org.blackerp.api.dto.CreateColumnRequest
import org.blackerp.domain.EntityMetadata
import org.blackerp.domain.table.ADTable
import org.blackerp.domain.table.ColumnDefinition
import org.blackerp.domain.values.*
import java.time.Instant
import java.util.UUID
import org.blackerp.api.dto.TableResponse

object TestFactory {
    fun createMetadata(
        id: UUID = TimeBasedId.generate(),
        createdBy: String = "test-user",
        updatedBy: String = createdBy,
        version: Int = 0,
        active: Boolean = true
    ) = EntityMetadata(
        id = id,
        created = Instant.now(),
        createdBy = createdBy,
        updated = Instant.now(),
        updatedBy = updatedBy,
        version = version,
        active = active
    )

    fun createTableResponse() = TableResponse(
        id = UUID.randomUUID(),
        name = "test_table",
        displayName = "Test Table",
        description = "Test Description",
        accessLevel = "SYSTEM"
    )

    fun createValidTableName(name: String = "test_table") = 
        TableName.create(name).getOrNull()
            ?: throw IllegalArgumentException("Invalid table name: $name")

    fun createValidDisplayName(name: String = "Test Table") = 
        DisplayName.create(name).getOrNull()
            ?: throw IllegalArgumentException("Invalid display name: $name")

    fun createValidDescription(text: String = "Test description") = 
        Description.create(text).getOrNull()

    private fun createDefaultColumn() = ColumnDefinition(
        metadata = createMetadata(),
        name = ColumnName.create("test_column").getOrNull()!!,
        displayName = DisplayName.create("Test Column").getOrNull()!!,
        description = null,
        dataType = DataType.STRING,
        length = Length.create(50).getOrNull(),
        precision = null,
        scale = null,
        mandatory = false,
        defaultValue = null
    )

    fun createTestTable(
        id: UUID = TimeBasedId.generate(),
        name: String = "test_table",
        displayName: String = "Test Table",
        description: String? = "Test Description",
        accessLevel: AccessLevel = AccessLevel.SYSTEM,
        createdBy: String = "test-user",
        updatedBy: String = createdBy,
        columns: List<ColumnDefinition> = emptyList()
    ): ADTable {
        val metadata = EntityMetadata(
            id = id,
            created = Instant.now(),
            createdBy = createdBy,
            updated = Instant.now(),
            updatedBy = updatedBy,
            version = 0,
            active = true
        )
        return ADTable(
            metadata = metadata,
            name = createValidTableName(name),
            displayName = createValidDisplayName(displayName),
            description = description?.let { createValidDescription(it) },
            accessLevel = accessLevel,
            columns = columns.ifEmpty { listOf(createDefaultColumn()) }
        )
    }

    fun createTableCommand(): CreateTableCommand {
        val columns = listOf(
            CreateColumnCommand(
                name = "test_column",
                displayName = "Test Column",
                description = "Test column description",
                dataType = DataType.STRING,
                length = 50,
                precision = null,
                scale = null
            )
        )

        return CreateTableCommand(
            name = "test_table",
            displayName = "Test Table",
            description = "Test Description",
            accessLevel = AccessLevel.SYSTEM,
            createdBy = "test-user",
            columns = columns
        )
    }

    fun createTableRequest() = CreateTableRequest(
        name = "test_table",
        displayName = "Test Table",
        description = "Test Description",
        accessLevel = "SYSTEM",
        columns = listOf(
            CreateColumnRequest(
                name = "test_column",
                displayName = "Test Column",
                description = "Test Column Description",
                dataType = "STRING",
                length = 50,
                precision = null,
                scale = null
            )
        )
    )
}