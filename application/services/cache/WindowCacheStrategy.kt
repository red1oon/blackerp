package org.blackerp.application.services.cache

import org.springframework.stereotype.Component
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.annotation.CacheEvict
import org.blackerp.domain.core.ad.window.ADWindow
import java.util.UUID

@Component
class WindowCacheStrategy {
    companion object {
        const val WINDOW_CACHE = "windows"
        const val WINDOW_BY_NAME_CACHE = "windows_by_name"
    }

    @Cacheable(value = [WINDOW_CACHE], key = "#id")
    suspend fun getWindow(id: UUID): ADWindow? = null  // Implementation provided by repository

    @CacheEvict(value = [WINDOW_CACHE, WINDOW_BY_NAME_CACHE], allEntries = true)
    suspend fun evictAll() {
        // Clear all window caches
    }
}
