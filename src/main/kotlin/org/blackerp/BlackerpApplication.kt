package org.blackerp

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BlackerpApplication

fun main(args: Array<String>) {
	runApplication<BlackerpApplication>(*args)
}
