package com.epaperlauncher.iconengine.domain.repository

import com.epaperlauncher.iconengine.domain.model.IconProcessingResult
import com.epaperlauncher.iconengine.domain.model.IconStyle
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for icon processing operations
 */
interface IconRepository {
    /**
     * Observe the file path for a processed icon
     * Returns null if not yet processed or cache miss
     */
    fun observeIconPath(packageName: String): Flow<String?>
    
    /**
     * Request icon processing for an app
     * Runs asynchronously via WorkManager
     */
    suspend fun requestProcessing(
        packageName: String,
        style: IconStyle = IconStyle.FLAT_MONOCHROME,
        forceRegenerate: Boolean = false
    ): IconProcessingResult
    
    /**
     * Invalidate cache for a specific app
     * Triggers re-processing on next request
     */
    suspend fun invalidateCache(packageName: String)
    
    /**
     * Clear entire icon cache
     * Use sparingly - e.g., when algorithm version changes
     */
    suspend fun clearAllCache()
    
    /**
     * Get cached icon path synchronously (for non-reactive use cases)
     */
    suspend fun getIconPath(packageName: String): String?
}
