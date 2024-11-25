package org.blackerp.domain

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class EntityMetadataTest : DescribeSpec({
    describe("EntityMetadata") {
        it("should create with default values") {
            val metadata = EntityMetadata(
                createdBy = "test-user",
                updatedBy = "test-user"
            )
            
            metadata.createdBy shouldBe "test-user"
            metadata.updatedBy shouldBe "test-user"
            metadata.version shouldBe 0
            metadata.active shouldBe true
            metadata.id shouldNotBe null
        }

        it("should create with custom values") {
            val metadata = EntityMetadata(
                createdBy = "test-user",
                updatedBy = "test-user",
                version = 1,
                active = false
            )
            
            metadata.version shouldBe 1
            metadata.active shouldBe false
        }
    }
})
