package com.epaperlauncher.feature.filterengine.presentation.components

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Build
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntSize
import androidx.core.graphics.createBitmap
import com.epaperlauncher.core.data.domain.model.ThemeConfig
import com.epaperlauncher.feature.filterengine.domain.model.CapturedFrame
import com.epaperlauncher.feature.filterengine.domain.model.FilterConfig
import com.epaperlauncher.feature.filterengine.domain.model.FilterResult
import com.epaperlauncher.feature.filterengine.presentation.shader.LegacyPaperFilter
import com.epaperlauncher.feature.filterengine.presentation.shader.PaperFilterShader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext

/**
 * Compose composable that renders the filter overlay.
 * This sits on top of all other content via TYPE_ACCESSIBILITY_OVERLAY.
 */
@Composable
fun FilterOverlayCanvas(
    config: ThemeConfig,
    onRequestMediaProjection: () -> Unit
) {
    val context = LocalContext.current
    
    // Current processed frame for rendering
    var processedFrame by remember { mutableStateOf<Bitmap?>(null) }
    
    // Filter shader instances
    val filterConfig = remember(config) {
        FilterConfig(
            contrast = config.contrastCurve.multiplier,
            grainIntensity = config.grainIntensity,
            inkColor = config.paperTone.toInkColor(),
            paperColor = config.paperTone.toPaperColor(),
            ditherEnabled = config.ditherEnabled,
            pageTurnAnimationEnabled = config.pageTurnAnimation
        )
    }

    val agslShader = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            PaperFilterShader(context, filterConfig)
        } else {
            null
        }
    }

    val legacyFilter = remember { LegacyPaperFilter(filterConfig) }

    // Simulated frame capture loop for Phase 0 spike
    // In production, this would use MediaProjection + VirtualDisplay
    LaunchedEffect(Unit) {
        // Request MediaProjection permission on start
        onRequestMediaProjection()
        
        // Placeholder: create a test bitmap to verify shader pipeline works
        val testBitmap = createTestBitmap()
        val capturedFrame = CapturedFrame(
            bitmap = testBitmap,
            timestamp = System.currentTimeMillis(),
            width = testBitmap.width,
            height = testBitmap.height,
            density = context.resources.displayMetrics.density
        )

        // Apply filter
        val result = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && agslShader != null) {
            agslShader.apply(capturedFrame)
        } else {
            legacyFilter.apply(capturedFrame)
        }

        when (result) {
            is FilterResult.Success -> {
                processedFrame = result.processedBitmap
            }
            is FilterResult.Error -> {
                // Log error, fallback to no filter
            }
            is FilterResult.PermissionRequired -> {
                // Permission flow handled by callback
            }
        }

        // Keep running to process new frames
        while (isActive) {
            delay(1000) // Placeholder: in production, process frames as they arrive
        }
    }

    // Render the processed frame
    androidx.compose.ui.graphics.Image(
        bitmap = processedFrame?.asImageBitmap() ?: return,
        contentDescription = null,
        modifier = Modifier.fillMaxSize()
    )
}

/**
 * Create a test bitmap with gradient for Phase 0 validation.
 */
private fun createTestBitmap(): Bitmap {
    val width = 1080
    val height = 2400
    val bitmap = createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    
    // Draw a gradient background
    val paint = Paint().apply {
        strokeWidth = 0f
    }
    
    for (y in 0 until height step 10) {
        val ratio = y.toFloat() / height
        val color = Color.argb(255, (ratio * 255).toInt(), ((1 - ratio) * 255).toInt(), 100)
        paint.color = color
        canvas.drawRect(0f, y.toFloat(), width.toFloat(), (y + 10).toFloat(), paint)
    }
    
    // Draw some text-like shapes
    paint.color = Color.BLACK
    for (i in 0..10) {
        val y = 200 + i * 150
        canvas.drawRect(100f, y.toFloat(), 900f, (y + 80).toFloat(), paint)
    }
    
    return bitmap
}

/**
 * Convert PaperTone to normalized ink color array.
 */
private fun com.epaperlauncher.core.data.domain.model.PaperTone.toInkColor(): FloatArray {
    return when (this) {
        com.epaperlauncher.core.data.domain.model.PaperTone.WARM -> 
            floatArrayOf(0.17f, 0.16f, 0.16f)
        com.epaperlauncher.core.data.domain.model.PaperTone.COLD -> 
            floatArrayOf(0.14f, 0.15f, 0.15f)
        com.epaperlauncher.core.data.domain.model.PaperTone.PURE_WHITE -> 
            floatArrayOf(0.0f, 0.0f, 0.0f)
    }
}

/**
 * Convert PaperTone to normalized paper color array.
 */
private fun com.epaperlauncher.core.data.domain.model.PaperTone.toPaperColor(): FloatArray {
    return when (this) {
        com.epaperlauncher.core.data.domain.model.PaperTone.WARM -> 
            floatArrayOf(0.96f, 0.94f, 0.92f)
        com.epaperlauncher.core.data.domain.model.PaperTone.COLD -> 
            floatArrayOf(0.95f, 0.95f, 0.96f)
        com.epaperlauncher.core.data.domain.model.PaperTone.PURE_WHITE -> 
            floatArrayOf(1.0f, 1.0f, 1.0f)
    }
}
