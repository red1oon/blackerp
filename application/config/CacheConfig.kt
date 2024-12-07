package org.blackerp.application.config

import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Bean
import org.springframework.cache.caffeine.CaffeineCacheManager
import com.github.benmanes.caffeine.cache.Caffeine
import java.util.concurrent.TimeUnit

@Configuration
@EnableCaching
class CacheConfig {
    @Bean
    fun cacheManager() = CaffeineCacheManager().apply {
        setCaffeine(Caffeine.newBuilder()
            .expireAfterWrite(30, TimeUnit.MINUTES)
            .maximumSize(100))
    }
}
