package org.blackerp.api.controllers

import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.DescribeSpec
import io.mockk.coEvery
import kotlinx.coroutines.test.runTest
import org.blackerp.application.table.CreateTableUseCase
import org.blackerp.api.mappers.TableMapper
import org.blackerp.shared.TestFactory
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.test.web.servlet.MockMvc
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import com.fasterxml.jackson.databind.ObjectMapper
import arrow.core.right

@WebMvcTest(TableController::class)
@AutoConfigureMockMvc(addFilters = false)
class TableControllerTest : DescribeSpec() {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var createTableUseCase: CreateTableUseCase

    @MockkBean
    private lateinit var tableMapper: TableMapper

    private val objectMapper = ObjectMapper()

    init {
        describe("TableController") {
            context("POST /api/tables") {
                it("should create table successfully") {
                    runTest {
                        // given
                        val request = TestFactory.createTableRequest()
                        val command = TestFactory.createTableCommand()
                        val table = TestFactory.createTestTable()
                        val response = TestFactory.createTableResponse()

                        // Configure mocks
                        coEvery { tableMapper.toCommand(request) } returns command
                        coEvery { createTableUseCase.execute(command) } returns table.right()
                        coEvery { tableMapper.toResponse(table) } returns response

                        // when/then
                        mockMvc.perform(
                            post("/api/tables")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                        ).andExpect(status().isOk)
                    }
                }
            }
        }
    }
}