// File: integration-tests/build.gradle.kts
plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    id("org.springframework.boot")
}

// Force build directory creation
project.buildDir.mkdirs()

sourceSets {
    test {
        kotlin {
            srcDirs("test/kotlin")
        }
        resources {
            srcDirs("test/resources")
        }
    }
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":application"))
    implementation(project(":infrastructure"))

    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("io.arrow-kt:arrow-core:1.2.0")

    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(module = "mockito-core")
    }
    testImplementation("com.h2database:h2")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.junit.jupiter:junit-jupiter-params")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("io.mockk:mockk:1.13.8")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.1.0")
}

// Create directories task
tasks.register("createDirs") {
    doLast {
        mkdir("${project.buildDir}/reports/tests/test")
        mkdir("${project.buildDir}/test-results/test")
    }
}

tasks.test {
    dependsOn("createDirs")
    useJUnitPlatform()

    // Force test execution
    outputs.upToDateWhen { false }

    // Ensure directories exist before testing
    doFirst {
        mkdir("${project.buildDir}/reports/tests/test")
        mkdir("${project.buildDir}/test-results/test")
    }

    testLogging {
        events("passed", "skipped", "failed", "standardOut", "standardError")
        showStandardStreams = true
        showStackTraces = true
        showExceptions = true
        showCauses = true
    }

    reports {
        html.destination = file("${project.buildDir}/reports/tests/test")
        junitXml.destination = file("${project.buildDir}/test-results/test")
    }
}

// Print directories for debugging
tasks.register("printDirs") {
    doLast {
        println("Build directory: ${project.buildDir}")
        println("Test report directory: ${project.buildDir}/reports/tests/test")
        println("Current directory: ${project.projectDir}")
    }
}

tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    enabled = false
}