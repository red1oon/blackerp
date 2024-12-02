sourceSets {
    main {
        kotlin {
            srcDirs("persistence/repositories", "cache/providers", "integration/adapters", "messaging/consumers", "plugin/loaders")
        }
    }
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":application"))
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("com.h2database:h2")
    implementation("org.flywaydb:flyway-core")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.8.0")
    implementation("org.slf4j:slf4j-api:2.0.7")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("io.arrow-kt:arrow-core:1.2.0")
}

tasks.bootJar {
    enabled = true
}

tasks.jar {
    enabled = true
}