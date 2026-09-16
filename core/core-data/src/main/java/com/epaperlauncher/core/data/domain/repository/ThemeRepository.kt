package com.epaperlauncher.core.data.domain.repository

import com.epaperlauncher.core.data.domain.model.FilterEngineState
import com.epaperlauncher.core.data.domain.model.ThemeConfig
import kotlinx.coroutines.flow.StateFlow

/**
 * Repository interface for theme configuration.
 */
interface ThemeRepository {
    /**
     * Observe the current theme config as StateFlow.
     */
    fun observeThemeConfig(): StateFlow<ThemeConfig>

    /**
     * Update theme config with a transform function.
     */
    suspend fun update(transform: (ThemeConfig) -> ThemeConfig)
}
