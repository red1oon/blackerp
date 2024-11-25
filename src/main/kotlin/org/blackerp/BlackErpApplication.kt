// File: src/main/kotlin/org/blackerp/BlackErpApplication.kt
package org.blackerp

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BlackErpApplication

fun main(args: Array<String>) {
    runApplication<BlackErpApplication>(*args)
}