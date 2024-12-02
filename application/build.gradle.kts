sourceSets {
    main {
        kotlin {
            srcDirs("api/advice", "api/controllers", "api/dto", "api/mappers", "services", "usecases")
        }
    }
}

dependencies {
    implementation(project(":domain"))
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
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