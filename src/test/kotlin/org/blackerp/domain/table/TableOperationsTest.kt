// File: src/test/kotlin/org/blackerp/domain/table/TableOperationsTest.kt
package org.blackerp.domain.table

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import org.blackerp.domain.EntityMetadata
import org.blackerp.domain.values.*
import org.blackerp.shared.TestFactory
import org.blackerp.infrastructure.persistence.store.InMemoryTableOperations

class TableOperationsTest : DescribeSpec({
    lateinit var operations: InMemoryTableOperations

    beforeTest {
        operations = InMemoryTableOperations()
    }

    describe("TableOperations") {
        context("saving and retrieving tables") {
            it("should save and retrieve table") {
                runTest {
                    // Create test table
                    val table = TestFactory.createTestTable()

                    // Save table
                    val savedResult = operations.save(table)
                    savedResult.isRight() shouldBe true

                    // Retrieve table
                    val retrievedResult = operations.findById(table.metadata.id)
                    retrievedResult.isRight() shouldBe true

                    val retrievedTable = retrievedResult.getOrNull()!!
                    retrievedTable.name.value shouldBe table.name.value
                    retrievedTable.displayName.value shouldBe table.displayName.value
                    retrievedTable.description?.value shouldBe table.description?.value
                }
            }
        }

        context("finding by name") {
            it("should find table by name") {
                runTest {
                    // Create and save test table
                    val table = TestFactory.createTestTable()
                    operations.save(table)

                    // Find by name
                    val foundResult = operations.findByName(table.name.value)
                    foundResult.isRight() shouldBe true

                    val foundTable = foundResult.getOrNull()!!
                    foundTable.metadata.id shouldBe table.metadata.id
                }
            }
        }
    }
})