// File: integration-tests/build.gradle.kts
plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    id("org.springframework.boot")
}

sourceSets {
    main {
        java.srcDirs("main/java")
        kotlin.srcDirs("main/kotlin")
        resources.srcDirs("main/resources")
    }
    test {
        java.srcDirs("test/java")
        kotlin.srcDirs("test/kotlin")
        resources.srcDirs("test/resources")
    }
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":application"))
    implementation(project(":infrastructure"))

    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(module = "mockito-core") // Use mockito-kotlin instead
    }
    testImplementation("com.h2database:h2")
    testImplementation("com.fasterxml.jackson.module:jackson-module-kotlin") // Added for ObjectMapper
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.junit.jupiter:junit-jupiter-params")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("io.mockk:mockk:1.13.8")
    testImplementation("ch.qos.logback:logback-classic")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.1.0")
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
        showExceptions = true
        showStackTraces = true
        showCauses = true
        showStandardStreams = true  // Added this to show test output
    }
}

// Disable bootJar for test project
tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    enabled = false
}