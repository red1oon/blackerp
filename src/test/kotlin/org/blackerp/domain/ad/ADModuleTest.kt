package org.blackerp.domain.ad

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.assertions.arrow.core.shouldBeRight
import org.blackerp.domain.ad.value.ModuleName
import org.blackerp.domain.values.DisplayName
import org.blackerp.domain.values.Description
import org.blackerp.plugin.Version
import org.blackerp.shared.TestFactory

class ADModuleTest : DescribeSpec({
    describe("ADModule") {
        describe("create") {
            it("should create valid module") {
                // Given
                val name = ModuleName.create("test-module").getOrNull()!!
                val displayName = DisplayName.create("Test Module").getOrNull()!!
                val description = Description.create("Test Description").getOrNull()!!
                val version = Version.create("1.0.0").getOrNull()!!

                val params = CreateModuleParams(
                    metadata = TestFactory.createMetadata(),
                    name = name,
                    displayName = displayName,
                    description = description,
                    version = version
                )

                // When
                val result = ADModule.create(params)

                // Then
                result.shouldBeRight().also { module ->
                    module.name shouldBe name
                    module.displayName shouldBe displayName
                    module.description shouldBe description
                    module.version shouldBe version
                }
            }
        }
    }
})
