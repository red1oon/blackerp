package org.blackerp.domain.values

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import arrow.core.Either

class TableNameTest : DescribeSpec({
    describe("TableName") {
        describe("create") {
            it("should create valid table name") {
                val result = TableName.create("valid_table_name")
                result.isRight() shouldBe true
            }
            
            it("should reject invalid format") {
                val result = TableName.create("Invalid Table")
                result.isLeft() shouldBe true
            }
            
            it("should reject name starting with number") {
                val result = TableName.create("1invalid")
                result.isLeft() shouldBe true
            }
        }
    }
})