// File: integration-tests/test/kotlin/org/blackerp/test/SimpleTest.kt
package org.blackerp.test

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory

class SimpleTest {
    private val logger = LoggerFactory.getLogger(SimpleTest::class.java)

    @BeforeEach
    fun setup() {
        logger.info("Setting up test")
    }

    @AfterEach
    fun tearDown() {
        logger.info("Tearing down test")
    }

    @Test
    fun `should pass simple test`() {
        logger.info("Running simple test")
        assert(true) { "This test should always pass" }
    }

    @Test
    fun `should show test output`() {
        println("This is a test message that should be visible")
        assert(true)
    }
}
