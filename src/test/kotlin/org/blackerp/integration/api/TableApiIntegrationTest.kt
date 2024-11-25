package org.blackerp.integration.api

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import org.blackerp.integration.IntegrationTestConfig
import org.springframework.context.annotation.Import
import org.blackerp.api.dto.CreateTableRequest
import org.blackerp.shared.TestFactory

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(IntegrationTestConfig::class)
class TableApiIntegrationTest(
    private val restTemplate: TestRestTemplate,
    @LocalServerPort private val port: Int
) : DescribeSpec({
    
    describe("Table API") {
        context("POST /api/tables") {
            it("should create table successfully") {
                // given
                val request = TestFactory.createTableRequest()
                
                // when
                val response = restTemplate.postForEntity(
                    "http://localhost:$port/api/tables",
                    request,
                    Any::class.java
                )
                
                // then
                response.statusCode shouldBe HttpStatus.OK
            }
        }
    }
})
