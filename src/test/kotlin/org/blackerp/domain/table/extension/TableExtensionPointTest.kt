package org.blackerp.domain.table.extension

import arrow.core.Either
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.blackerp.domain.table.ADTable
import org.blackerp.plugin.PluginId

class TableExtensionPointTest : DescribeSpec({
    describe("TableExtension") {
        val mockTable = mockk<ADTable>()
        val mockPluginId = PluginId.create("test-plugin").getOrNull()!!
        it("should implement extension interface") {
            val extension = object : TableExtension {
                override val pluginId = mockPluginId
                override suspend fun beforeCreate(table: ADTable) = Either.Right(table)
                override suspend fun afterCreate(table: ADTable) {}
                override suspend fun beforeUpdate(table: ADTable) = Either.Right(table)
                override suspend fun afterUpdate(table: ADTable) {}
                override suspend fun beforeDelete(table: ADTable) = Either.Right(Unit)
                override suspend fun afterDelete(table: ADTable) {}
            }

            runTest {
                extension.beforeCreate(mockTable).isRight() shouldBe true
            }
        }
    }
})
