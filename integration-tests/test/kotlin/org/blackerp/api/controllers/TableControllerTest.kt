package org.blackerp.api.controllers

import org.blackerp.test.IntegrationTest
import org.blackerp.application.api.dto.requests.CreateTableRequest
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

class TableControllerTest : IntegrationTest() {
    
    @Test
    fun `should create table with valid request`() {
        val request = CreateTableRequest(
            name = "test_table",
            displayName = "Test Table",
            description = "Test description",
            accessLevel = "ORGANIZATION"
        )

        mockMvc.perform(
            post("/api/tables")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.name").value(request.name))
            .andExpect(jsonPath("$.displayName").value(request.displayName))
    }

    @Test
    fun `should return all tables`() {
        mockMvc.perform(get("/api/tables"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)
    }
}
