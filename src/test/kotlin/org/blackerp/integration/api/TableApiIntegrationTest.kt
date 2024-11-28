// File: src/test/kotlin/org/blackerp/integration/api/TableApiIntegrationTest.kt
package org.blackerp.integration.api

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.http.MediaType
import org.springframework.beans.factory.annotation.Autowired
import io.kotest.core.spec.style.DescribeSpec
import io.mockk.coEvery
import kotlinx.coroutines.test.runTest
import arrow.core.right
import org.blackerp.shared.TestFactory
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import org.blackerp.domain.table.TableOperations
import org.blackerp.application.table.CreateTableUseCase
import org.blackerp.api.mappers.TableMapper
import org.blackerp.api.controllers.TableController
import java.io.File

@WebMvcTest(TableController::class)
@AutoConfigureMockMvc(addFilters = false) 
class TableApiIntegrationTest @Autowired constructor(
    private val mockMvc: MockMvc,
    private val objectMapper: ObjectMapper,
    @MockkBean private val tableOperations: TableOperations,
    @MockkBean private val createTableUseCase: CreateTableUseCase,
    @MockkBean private val tableMapper: TableMapper
) : DescribeSpec({

    beforeTest {
        File("testdebug.txt").writeText("")
    }

    describe("POST /api/tables") {
        it("should create table successfully") {
            runTest {
                val request = TestFactory.createTableRequest()
                val command = TestFactory.createTableCommand()
                val table = TestFactory.createTestTable()
                val response = TestFactory.createTableResponse()

                val requestJson = objectMapper.writeValueAsString(request)
                val expectedJson = objectMapper.writeValueAsString(response)

                File("testdebug.txt").appendText("""
                    POST Request: $requestJson
                    Expected POST: $expectedJson
                """.trimIndent())

                coEvery { tableMapper.toCommand(request) } returns command
                coEvery { createTableUseCase.execute(command) } returns table.right()
                coEvery { tableMapper.toResponse(table) } returns response

                val result = mockMvc.perform(post("/api/tables")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestJson))
                    .andExpect(status().isOk)
                    .andReturn()

                File("testdebug.txt").appendText("\nActual POST: ${result.response.contentAsString}")
            }
        }
    }
})