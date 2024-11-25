// File: src/test/kotlin/org/blackerp/config/KotestProjectConfig.kt
package org.blackerp.config

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.spec.IsolationMode
import io.kotest.extensions.spring.SpringExtension

class KotestProjectConfig : AbstractProjectConfig() {
    override val isolationMode = IsolationMode.InstancePerLeaf
    override fun extensions() = listOf(SpringExtension)
}