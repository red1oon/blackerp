package org.blackerp.application.services

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executors

@Configuration
class CoroutineConfig {
    @Bean
    fun ioDispatcher(): CoroutineDispatcher = 
        Executors.newFixedThreadPool(10).asCoroutineDispatcher()
}
