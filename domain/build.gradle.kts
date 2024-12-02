sourceSets {
    main {
        kotlin {
            srcDirs("core/ad", "core/client", "core/error", "core/metadata", "core/rules", "core/security", "core/tenant", "core/values", "events", "validation/validators")
        }
    }
}

dependencies {
    implementation("org.springframework:spring-context")
    implementation("jakarta.validation:jakarta.validation-api")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.8.0")
    implementation("org.slf4j:slf4j-api:2.0.7")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("io.arrow-kt:arrow-core:1.2.0")
}

tasks.bootJar {
    enabled = false
}

tasks.jar {
    enabled = true
}