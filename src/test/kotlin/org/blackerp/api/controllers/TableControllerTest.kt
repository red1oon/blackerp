// File: src/test/kotlin/org/blackerp/api/controllers/TableControllerTest.kt
package org.blackerp.api.controllers

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

@WebMvcTest(TableController::class)
@AutoConfigureMockMvc(addFilters = false)
class TableControllerTest(
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val objectMapper: ObjectMapper,
    @MockkBean private val tableOperations: TableOperations,
    @MockkBean private val createTableUseCase: CreateTableUseCase,
    @MockkBean private val tableMapper: TableMapper
) : DescribeSpec({

    describe("POST /api/tables") {
        it("should create table successfully") {
            runTest {
                val request = TestFactory.createTableRequest()
                val command = TestFactory.createTableCommand()
                val table = TestFactory.createTestTable()
                val response = TestFactory.createTableResponse()

                coEvery { tableMapper.toCommand(request) } returns command
                coEvery { createTableUseCase.execute(command) } returns table.right()
                coEvery { tableMapper.toResponse(table) } returns response

                mockMvc.perform(post("/api/tables")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk)
            }
        }
    }
})