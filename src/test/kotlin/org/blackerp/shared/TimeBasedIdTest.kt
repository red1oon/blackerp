package org.blackerp.shared

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.util.UUID

class TimeBasedIdTest : DescribeSpec({
    describe("TimeBasedId") {
        describe("generate") {
            it("should generate time-based UUIDs") {
                // Generate two IDs with a small delay
                val id1 = TimeBasedId.generate()
                Thread.sleep(1) // Minimal delay
                val id2 = TimeBasedId.generate()

                // Verify they are different
                id1 shouldNotBe id2

                // Verify version 1 (time-based) UUID
                (id1.version() == 1) shouldBe true
                (id2.version() == 1) shouldBe true
            }
        }
    }
})