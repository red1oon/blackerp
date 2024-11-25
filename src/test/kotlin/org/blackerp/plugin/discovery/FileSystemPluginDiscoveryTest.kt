package org.blackerp.plugin.discovery

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import java.nio.file.Files
import kotlin.io.path.createTempDirectory
import kotlin.io.path.writeText

class FileSystemPluginDiscoveryTest : DescribeSpec({
    describe("FileSystemPluginDiscovery") {
        val discovery = FileSystemPluginDiscovery()

        it("should discover jar files") {
            runTest {
                val tempDir = createTempDirectory()
                val jarFile = tempDir.resolve("test.jar")
                jarFile.writeText("dummy jar content")

                val result = discovery.discoverPlugins(tempDir)
                result.isRight() shouldBe true

                Files.deleteIfExists(jarFile)
                Files.deleteIfExists(tempDir)
            }
        }
    }
})
