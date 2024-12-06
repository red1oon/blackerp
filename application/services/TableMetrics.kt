package org.blackerp.application.services.metrics

import org.springframework.stereotype.Component
import io.micrometer.core.instrument.MeterRegistry
import java.time.Duration
import java.util.concurrent.TimeUnit

@Component
class TableMetrics(private val meterRegistry: MeterRegistry) {
    private val createTableCounter = meterRegistry.counter("table.create.count")
    private val createTableTimer = meterRegistry.timer("table.create.duration")

    fun incrementCreateCounter() = createTableCounter.increment()
    
    fun <T> timeCreateOperation(operation: () -> T): T {
        val start = System.nanoTime()
        return try {
            operation()
        } finally {
            val duration = System.nanoTime() - start
            createTableTimer.record(duration, TimeUnit.NANOSECONDS)
        }
    }
}
