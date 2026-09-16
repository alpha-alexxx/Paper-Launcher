package com.epaperlauncher.feature.filterengine.data

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.provider.Settings
import android.view.Display
import android.view.Surface
import android.view.WindowManager
import androidx.compose.ui.platform.ComposeView
import androidx.core.content.ContextCompat
import com.epaperlauncher.core.data.domain.model.FilterEngineState
import com.epaperlauncher.core.data.domain.repository.ThemeRepository
import com.epaperlauncher.feature.filterengine.di.ServiceEntryPoint
import com.epaperlauncher.feature.filterengine.presentation.components.FilterOverlayCanvas
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Accessibility Service that manages the filter overlay.
 * Required to legally draw a persistent overlay across other apps and detect window changes.
 */
@AndroidEntryPoint
class FilterOverlayService : AccessibilityService() {

    @Inject
    lateinit var themeRepository: ThemeRepository

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var windowManager: WindowManager? = null
    private var overlayView: ComposeView? = null

    companion object {
        private var isServiceRunning = false

        fun isServiceEnabled(context: Context): Boolean {
            val serviceName = FilterOverlayService::class.java.canonicalName ?: return false
            val enabledServices = try {
                Settings.Secure.getString(
                    context.contentResolver,
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
                ) ?: ""
            } catch (e: Exception) {
                ""
            }
            return enabledServices.contains(context.packageName) || enabledServices.contains(serviceName)
        }

        fun isRunning(): Boolean = isServiceRunning
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        isServiceRunning = true
        
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        overlayView = ComposeView(this).apply {
            // Collect theme config and render overlay
            serviceScope.launch {
                val config = themeRepository.observeThemeConfig().first()
                
                setContent {
                    FilterOverlayCanvas(
                        config = config,
                        onRequestMediaProjection = {
                            launchMediaProjectionRequest()
                        }
                    )
                }
            }
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            PixelFormat.TRANSLUCENT
        )

        try {
            windowManager?.addView(overlayView, params)
        } catch (e: Exception) {
            // Handle case where overlay permission not granted
        }
    }

    override fun onAccessibilityEvent(event: android.view.accessibility.AccessibilityEvent) {
        when (event.eventType) {
            android.view.accessibility.AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                // Trigger page-turn animation on app switch
                if (event.packageName != packageName) {
                    // Different app launched - trigger transition
                    triggerPageTurnAnimation()
                }
            }
        }
    }

    override fun onInterrupt() {
        // Required callback, no-op for our use case
    }

    override fun onDestroy() {
        super.onDestroy()
        isServiceRunning = false
        overlayView?.let {
            try {
                windowManager?.removeView(it)
            } catch (e: Exception) {
                // View may already be removed
            }
        }
        overlayView = null
        windowManager = null
        serviceScope.cancel()
    }

    /**
     * Launch MediaProjection screen capture request.
     * This is required for the Tier 1 non-root filter to actually process screen content.
     */
    private fun launchMediaProjectionRequest() {
        val mediaProjectionManager = 
            ContextCompat.getSystemService(this, MediaProjectionManager::class.java)
                ?: return

        val intent = mediaProjectionManager.createScreenCaptureIntent()
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    /**
     * Trigger page-turn transition animation.
     */
    private fun triggerPageTurnAnimation() {
        // TODO: Implement shader-driven page curl effect
        // For Phase 0, this is a placeholder
    }
}
