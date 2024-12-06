// /test-import/domain/build.gradle.kts
plugins {
    kotlin("jvm")
}

kotlin {
    jvmToolchain(17)
}

sourceSets {
    main {
        kotlin {
            // Explicitly list all source directories
            srcDirs(
                "core",
                "core/metadata",
                "core/shared",
                "core/values"
            )
        }
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("io.arrow-kt:arrow-core:1.2.0")
}