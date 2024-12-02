sourceSets {
    main {
        kotlin {
            srcDirs(
                "core/ad",
                "core/client", 
                "core/error",
                "core/metadata",
                "core/rules",
                "core/security",
                "core/tenant",
                "core/values",
                "events",
                "validation/validators"
            )
        }
    }
}

dependencies {
    implementation("org.springframework:spring-context")
    implementation("jakarta.validation:jakarta.validation-api")
}

tasks.bootJar {
    enabled = false
}

tasks.jar {
    enabled = true
}
