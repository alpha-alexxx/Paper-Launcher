package com.epaperlauncher.feature.filterengine.data.impl

import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.provider.Settings
import androidx.core.content.ContextCompat
import com.epaperlauncher.core.data.domain.model.FilterEngineState
import com.epaperlauncher.core.data.domain.repository.FilterEngineController
import com.epaperlauncher.feature.filterengine.data.FilterOverlayService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Implementation of [FilterEngineController] that manages the filter service lifecycle.
 */
class FilterEngineControllerImpl(
    private val context: Context
) : FilterEngineController {

    private val _engineState = MutableStateFlow(FilterEngineState.DISABLED)
    override fun observeEngineState(): StateFlow<FilterEngineState> = _engineState.asStateFlow()

    override fun hasOverlayPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(context)
        } else {
            true
        }
    }

    override fun isAccessibilityServiceEnabled(): Boolean {
        return FilterOverlayService.isServiceEnabled(context)
    }

    override suspend fun requestEnable(): FilterEngineState {
        return when {
            !hasOverlayPermission() -> {
                // Request overlay permission via intent
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    android.net.Uri.parse("package:${context.packageName}")
                ).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
                FilterEngineState.PERMISSION_REQUIRED
            }
            !isAccessibilityServiceEnabled() -> {
                // Guide user to accessibility settings
                val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
                FilterEngineState.PERMISSION_REQUIRED
            }
            else -> {
                // Start the filter service
                val intent = Intent(context, FilterOverlayService::class.java)
                ContextCompat.startForegroundService(context, intent)
                _engineState.value = FilterEngineState.ACTIVE
                FilterEngineState.ACTIVE
            }
        }
    }

    override fun disable() {
        val intent = Intent(context, FilterOverlayService::class.java)
        context.stopService(intent)
        _engineState.value = FilterEngineState.DISABLED
    }

    override fun setPerAppOverride(packageName: String, enabled: Boolean) {
        // TODO: Persist to DataStore via ThemeRepository
        // For Phase 0 spike, this is a no-op placeholder
    }

    /**
     * Update engine state from external sources (e.g., service callbacks).
     */
    fun updateState(state: FilterEngineState) {
        _engineState.value = state
    }
}
