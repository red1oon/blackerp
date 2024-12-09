plugins {
    kotlin("jvm") version "1.9.20"
}

tasks.bootJar {
    enabled = false
}

tasks.jar {
    enabled = true
}

sourceSets {
    main {
        kotlin {
            srcDirs(
                // Existing directories
                "core",
                "events", 
                "validation",
                
                // New metadata directories
                "core/ad/metadata",
                "core/ad/metadata/entities",
                "core/ad/metadata/operations", 
                "core/ad/metadata/services"
            )
        }
    }
}

dependencies {
    implementation("org.slf4j:slf4j-api:2.0.9")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    api("io.arrow-kt:arrow-core:1.1.3")
    // No other dependencies needed - domain is pure
}