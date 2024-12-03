package org.blackerp.domain.core.ad.process

sealed interface ProcessImplementation {
    data class JavaClass(val className: String) : ProcessImplementation
    data class DatabaseFunction(val functionName: String) : ProcessImplementation
    data class Script(val language: String, val code: String) : ProcessImplementation
}

data class ProcessSchedule(
    val cronExpression: String,
    val enabled: Boolean = true
)
