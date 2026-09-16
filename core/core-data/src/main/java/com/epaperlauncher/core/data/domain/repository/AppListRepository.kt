package com.epaperlauncher.core.data.domain.repository

import com.epaperlauncher.core.data.domain.model.AppEntry
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for app list operations.
 * Implemented in core-data module, consumed by feature modules.
 */
interface AppListRepository {
    /**
     * Observe the full list of apps as a Flow.
     */
    fun observeApps(): Flow<List<AppEntry>>

    /**
     * Force refresh from PackageManager (called on cold start or package changes).
     */
    suspend fun refreshFromPackageManager()

    /**
     * Hide or unhide an app from the launcher.
     */
    suspend fun setHidden(packageName: String, hidden: Boolean)

    /**
     * Pin or unpin an app to the top of the list.
     */
    suspend fun setPinned(packageName: String, pinned: Boolean)

    /**
     * Record an app launch for usage-based sorting.
     */
    suspend fun recordLaunch(packageName: String)

    /**
     * Search apps by label.
     */
    suspend fun searchApps(query: String): List<AppEntry>
}
