package org.blackerp.infrastructure.cache

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import java.time.Duration

class InMemoryCacheServiceTest : DescribeSpec({
    lateinit var cacheService: CacheService

    beforeTest {
        cacheService = InMemoryCacheService()
    }

    describe("InMemoryCacheService") {
        it("should store and retrieve values") {
            runTest {
                cacheService.set("test", "value")
                val result = cacheService.get<String>("test")
                result.getOrNull() shouldBe "value"
            }
        }

        it("should handle TTL") {
            runTest {
                cacheService.set("test", "value", Duration.ofMillis(1))
                Thread.sleep(10)
                val result = cacheService.get<String>("test")
                result.getOrNull() shouldBe null
            }
        }
    }
})
