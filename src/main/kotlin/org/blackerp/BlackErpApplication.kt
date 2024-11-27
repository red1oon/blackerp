package org.blackerp

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(
    scanBasePackages = ["org.blackerp"]
)
class BlackErpApplication

fun main(args: Array<String>) {
    runApplication<BlackErpApplication>(*args)
}
