package org.blackerp.shared
 
import org.blackerp.api.dto.request.CreateTableRequest
import org.blackerp.api.dto.request.CreateColumnRequest
import org.blackerp.api.dto.response.TableResponse
import org.blackerp.application.table.CreateTableCommand
import org.blackerp.application.table.CreateColumnCommand
import org.blackerp.domain.EntityMetadata
import org.blackerp.domain.table.ADTable
import org.blackerp.domain.table.ColumnDefinition
import org.blackerp.domain.values.*
import java.time.Instant
import java.util.UUID

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

    fun createValidTableName(name: String = "test_table") = 
        TableName.create(name).getOrNull()
            ?: throw IllegalStateException("Failed to create valid table name: $name")

    fun createValidDisplayName(name: String = "Test Table") = 
        DisplayName.create(name).getOrNull()
            ?: throw IllegalStateException("Failed to create valid display name: $name")

    fun createValidDescription(text: String = "Test description") = 
        Description.create(text).getOrNull()

    fun createTestColumn(): ColumnDefinition {
        val metadata = createMetadata()
        val name = ColumnName.create("test_column").getOrNull()!!
        val displayName = DisplayName.create("Test Column").getOrNull()!!
        val description = Description.create("Test column description").getOrNull()
        val length = Length.create(50).getOrNull()!!

        return ColumnDefinition(
            metadata = metadata,
            name = name,
            displayName = displayName,
            description = description,
            dataType = DataType.STRING,
            length = length,
            precision = null,
            scale = null,
            mandatory = false,
            defaultValue = null
        )
    }

    // Add inside TestFactory object
    fun createTableRequest() = CreateTableRequest(
        name = "test_table",
        displayName = "Test Table",
        description = "Test Description",
        accessLevel = "SYSTEM",
        columns = listOf(
            CreateColumnRequest(
                name = "test_column",
                displayName = "Test Column",
                description = "Test column description",
                dataType = "STRING",
                length = 50,
                precision = null,
                scale = null
            )
        )
    )

    fun createTableCommand() = CreateTableCommand(
        name = "test_table",
        displayName = "Test Table",
        description = "Test Description",
        accessLevel = AccessLevel.SYSTEM,
        createdBy = "test-user",
        columns = listOf(
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
    )

    fun createTableResponse() = TableResponse(
        id = UUID.randomUUID(),
        name = "test_table",
        displayName = "Test Table",
        description = "Test Description",
        accessLevel = "SYSTEM"
    )

    fun createTestTable(
        id: UUID = TimeBasedId.generate(),
        name: String = "test_table",
        displayName: String = "Test Table",
        description: String? = "Test Description",
        accessLevel: AccessLevel = AccessLevel.SYSTEM,
        createdBy: String = "test-user",
        updatedBy: String = createdBy
    ): ADTable {
        val metadata = createMetadata(
            id = id,
            createdBy = createdBy,
            updatedBy = updatedBy
        )

        return ADTable(
            metadata = metadata,
            name = createValidTableName(name),
            displayName = createValidDisplayName(displayName),
            description = description?.let { createValidDescription(it) },
            accessLevel = accessLevel,
            columns = listOf(createTestColumn())
        )
    }
}
