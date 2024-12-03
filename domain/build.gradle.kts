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
    implementation("com.fasterxml.jackson.core:jackson-databind:2.14.2")
    implementation("jakarta.persistence:jakarta.persistence-api:3.1.0")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.8.0")
}

tasks.bootJar {
    enabled = false
}

tasks.jar {
    enabled = true
}