sourceSets {
    main {
        kotlin {
            srcDirs(
                "persistence/repositories",
                "cache/providers",
                "integration/adapters",
                "messaging/consumers", 
                "plugin/loaders"
            )
        }
    }
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":application"))
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("com.h2database:h2")
    implementation("org.flywaydb:flyway-core")
}

tasks.bootJar {
    enabled = true
}

tasks.jar {
    enabled = true
}