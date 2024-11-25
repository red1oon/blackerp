
package org.blackerp.domain.values

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.assertions.throwables.shouldThrow

class DataTypeTest : DescribeSpec({
    describe("DataType") {
        describe("fromString") {
            it("should create valid data type from string") {
                DataType.fromString("STRING") shouldBe DataType.STRING
                DataType.fromString("string") shouldBe DataType.STRING
            }
            
            it("should throw exception for invalid type") {
                shouldThrow<IllegalArgumentException> {
                    DataType.fromString("INVALID_TYPE")
                }
            }
        }
    }
})
