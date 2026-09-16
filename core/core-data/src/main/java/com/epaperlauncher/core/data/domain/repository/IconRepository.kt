package com.epaperlauncher.core.data.domain.repository

import com.epaperlauncher.core.data.domain.model.IconStyle
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for icon processing and caching.
 */
interface IconRepository {
    /**
     * Observe the icon path for a specific package.
     * Emits null if not yet processed.
     */
    fun observeIconPath(packageName: String): Flow<String?>

    /**
     * Request icon processing for an app.
     */
    suspend fun requestProcessing(packageName: String, style: IconStyle)

    /**
     * Invalidate cache for a specific app (e.g., after app update).
     */
    suspend fun invalidateCache(packageName: String)

    /**
     * Get cached icon path synchronously (for Compose image loading).
     */
    fun getCachedIconPath(packageName: String): String?
}
