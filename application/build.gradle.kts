sourceSets {
    main {
        kotlin {
            srcDirs(
                "api/advice",
                "api/controllers", 
                "api/dto",
                "api/mappers",
                "services",
                "usecases"
            )
        }
    }
}

dependencies {
    implementation(project(":domain"))
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
}

tasks.bootJar {
    enabled = false
}

tasks.jar {
    enabled = true
}
