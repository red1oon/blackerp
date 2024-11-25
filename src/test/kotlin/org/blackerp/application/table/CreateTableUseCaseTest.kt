package org.blackerp.application.table

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.slot
import arrow.core.Either
import arrow.core.right
import kotlinx.coroutines.test.runTest
import org.blackerp.domain.values.AccessLevel
import org.blackerp.domain.table.ADTable
import org.blackerp.domain.values.DataType
import org.blackerp.domain.table.TableError
import org.blackerp.domain.table.TableOperations
import org.blackerp.infrastructure.event.EventPublisher

class CreateTableUseCaseTest : DescribeSpec({
    lateinit var operations: TableOperations
    lateinit var eventPublisher: EventPublisher
    lateinit var useCase: CreateTableUseCase

    beforeTest {
        operations = mockk(relaxed = true) {
            coEvery { save(any()) } returns mockk<ADTable>().right()
        }
        eventPublisher = mockk(relaxed = true)
        useCase = CreateTableUseCase(operations, eventPublisher)
    }

    describe("execute") {
        context("with valid command") {
            val columns = listOf(
                CreateColumnCommand(
                    name = "column_name",
                    displayName = "Column Name",
                    description = "A test column",
                    dataType = DataType.STRING,
                    length = 50,
                    precision = null,
                    scale = null
                )
            )

            val command = CreateTableCommand(
                name = "test_table",
                displayName = "Test Table",
                description = "A test table",
                accessLevel = AccessLevel.SYSTEM,
                createdBy = "test-user",
                columns = columns
            )

            it("should create and store table") {
                runTest {
                    // given
                    val tableSlot = slot<ADTable>()
                    coEvery { operations.save(capture(tableSlot)) } answers { 
                        tableSlot.captured.right() 
                    }

                    // when
                    val result = useCase.execute(command)

                    // then
                    result.fold(
                        { error -> throw AssertionError("Should not fail: $error") },
                        { table ->
                            table.name.value shouldBe "test_table"
                            table.displayName.value shouldBe "Test Table"
                            table.description?.value shouldBe "A test table"
                            table.accessLevel shouldBe AccessLevel.SYSTEM
                            table.metadata.createdBy shouldBe "test-user"
                        }
                    )
                }
            }
        }

        context("with invalid command") {
            val columns = listOf(
                CreateColumnCommand(
                    name = "column_name",
                    displayName = "",
                    description = "A test column with an invalid name",
                    dataType = DataType.STRING,
                    length = 50,
                    precision = null,
                    scale = null
                )
            )

            val command = CreateTableCommand(
                name = "Invalid Name!",
                displayName = "",
                description = "A".repeat(300),
                accessLevel = AccessLevel.SYSTEM,
                createdBy = "test-user",
                columns = columns
            )

            it("should return validation errors") {
                runTest {
                    // when
                    val result = useCase.execute(command)

                    // then
                    result.fold(
                        { error ->
                            error.shouldBeTypeOf<TableError.ValidationFailed>()
                            error.errors.size shouldBe 3
                        },
                        { throw AssertionError("Should not succeed") }
                    )
                }
            }
        }
    }
})