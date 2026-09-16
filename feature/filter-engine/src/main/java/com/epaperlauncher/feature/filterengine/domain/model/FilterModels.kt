package com.epaperlauncher.feature.filterengine.domain.model

import android.graphics.Bitmap

/**
 * Configuration for the paper filter shader pipeline.
 */
data class FilterConfig(
    val contrast: Float = 1.0f,
    val grainIntensity: Float = 0.1f,
    val inkColor: FloatArray = floatArrayOf(0.17f, 0.16f, 0.16f), // Warm ink normalized RGB
    val paperColor: FloatArray = floatArrayOf(0.96f, 0.94f, 0.92f), // Warm paper normalized RGB
    val ditherEnabled: Boolean = false,
    val pageTurnAnimationEnabled: Boolean = true
)

/**
 * Captured frame from MediaProjection with metadata.
 */
data class CapturedFrame(
    val bitmap: Bitmap,
    val timestamp: Long,
    val width: Int,
    val height: Int,
    val density: Float
) {
    fun recycle() {
        if (!bitmap.isRecycled) {
            bitmap.recycle()
        }
    }
}

/**
 * Result of filter processing.
 */
sealed class FilterResult {
    data class Success(val processedBitmap: Bitmap) : FilterResult()
    data class Error(val message: String, val exception: Throwable? = null) : FilterResult()
    object PermissionRequired : FilterResult()
}
