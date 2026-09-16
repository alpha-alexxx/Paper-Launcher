package com.epaperlauncher.core.data.domain.repository

import com.epaperlauncher.core.data.domain.model.FilterEngineState

/**
 * Repository interface bridging Compose settings UI and the AccessibilityService.
 * The service lives outside the Compose tree, so this uses StateFlow for communication.
 */
interface FilterEngineController {
    /**
     * Observe the current engine state.
     */
    fun observeEngineState(): StateFlow<FilterEngineState>

    /**
     * Request to enable the filter (may trigger permission flows).
     */
    suspend fun requestEnable(): FilterEngineState

    /**
     * Disable the filter.
     */
    fun disable()

    /**
     * Set per-app filter override.
     */
    fun setPerAppOverride(packageName: String, enabled: Boolean)

    /**
     * Check if overlay permission is granted.
     */
    fun hasOverlayPermission(): Boolean

    /**
     * Check if accessibility service is enabled.
     */
    fun isAccessibilityServiceEnabled(): Boolean
}
