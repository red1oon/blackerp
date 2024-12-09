package org.blackerp.test.fixtures

import org.blackerp.domain.core.ad.table.*
import org.blackerp.test.builders.ADTableBuilder
import java.util.UUID

object ADFixtures {
    fun createBasicTable(name: String = "test_table") = ADTableBuilder()
        .withName(name)
        .withDisplayName("Test Table")
        .withDescription("Test Description")
        .withAccessLevel(AccessLevel.ORGANIZATION)
        .build()

    fun createTableWithColumns(name: String = "test_table") = ADTableBuilder()
        .withName(name)
        .withDisplayName("Test Table")
        .withColumns(listOf(
            ColumnDefinition(
                name = "id",
                dataType = "UUID"
            ),
            ColumnDefinition(
                name = "name",
                dataType = "STRING",
                length = 100
            )
        ))
        .build()
}
