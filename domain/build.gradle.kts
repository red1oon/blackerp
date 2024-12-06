plugins {
    kotlin("jvm")
}

tasks.bootJar {
    enabled = false
}

tasks.jar {
    enabled = true
}

sourceSets {
    main {
        kotlin.srcDirs("core", "events", "validation")
    }
}

dependencies {
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    api("io.arrow-kt:arrow-core:1.1.3")
     
}
