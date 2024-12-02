package org.blackerp.application.services

import org.springframework.stereotype.Component
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Timer
import org.slf4j.LoggerFactory

@Component
class TableMetrics(private val meterRegistry: MeterRegistry) {
    private val logger = LoggerFactory.getLogger(TableMetrics::class.java)

    private val createTableCounter = meterRegistry.counter("table.create.count")
    private val modifyTableCounter = meterRegistry.counter("table.modify.count")
    private val deleteTableCounter = meterRegistry.counter("table.delete.count")
    private val createTableTimer = meterRegistry.timer("table.create.duration")
    private val errorCounter = meterRegistry.counter("table.error.count")

    fun incrementCreateCounter() {
        createTableCounter.increment()
        logger.debug("Table creation counter incremented")
    }

    fun incrementModifyCounter() {
        modifyTableCounter.increment()
        logger.debug("Table modification counter incremented")
    }

    fun incrementDeleteCounter() {
        deleteTableCounter.increment()
        logger.debug("Table deletion counter incremented")
    }

    fun incrementErrorCounter() {
        errorCounter.increment()
        logger.debug("Error counter incremented")
    }

    fun <T> timeCreateOperation(block: () -> T): T {
        return createTableTimer.record<T> {
            try {
                block()
            } catch (e: Exception) {
                incrementErrorCounter()
                throw e
            }
        }
    }
}
