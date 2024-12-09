plugins {
    kotlin("jvm") version "1.9.20"
    kotlin("plugin.spring")
    id("org.springframework.boot")
}

tasks.bootJar {
    enabled = false
}

tasks.jar {
    enabled = true
}

sourceSets {
    main {
        kotlin.srcDirs(
            "persistence/repositories",
            "cache/providers",
            "integration/adapters",
            "messaging/consumers",
            "persistence/entities",
            "persistence/mappers",
            "events/publishers",
            "plugin/loaders"
        )
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator:3.2.0")
    implementation(project(":domain")) // Only depends on domain
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("com.h2database:h2")
    implementation("org.flywaydb:flyway-core")
}
