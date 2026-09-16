package com.epaperlauncher.feature.filterengine.presentation.shader

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import android.graphics.Shader
import android.os.Build
import androidx.annotation.RequiresApi
import com.epaperlauncher.feature.filterengine.domain.model.CapturedFrame
import com.epaperlauncher.feature.filterengine.domain.model.FilterConfig
import com.epaperlauncher.feature.filterengine.domain.model.FilterResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * AGSL-based paper filter shader for API 33+.
 * Implements the e-ink visual pipeline: grayscale → contrast → paper tone mapping → grain.
 */
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
class PaperFilterShader(private val context: Context, private val config: FilterConfig) {

    private val shaderSource: String by lazy {
        context.resources.openRawResource(R.raw.paper_filter_shader).bufferedReader().use { it.readText() }
    }

    private lateinit var runtimeShader: RuntimeShader
    private lateinit var grainBitmap: Bitmap

    init {
        generateGrainTexture()
        initializeShader()
    }

    /**
     * Generate a tileable noise texture for grain overlay.
     * Uses Perlin-like noise generated once and cached.
     */
    private fun generateGrainTexture() {
        val size = 256
        grainBitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        
        val pixels = IntArray(size * size)
        for (y in 0 until size) {
            for (x in 0 until size) {
                // Simple random noise with edge wrapping for tileability
                val noise = (Math.random() * 255).toInt()
                val alpha = (config.grainIntensity * 255).toInt().coerceIn(0, 76) // Max ~30% intensity
                pixels[y * size + x] = Color.argb(alpha, noise, noise, noise)
            }
        }
        grainBitmap.setPixels(pixels, 0, size, 0, 0, size, size)
    }

    /**
     * Initialize the RuntimeShader with uniform values.
     */
    private fun initializeShader() {
        runtimeShader = RuntimeShader(shaderSource)
        updateUniforms()
    }

    /**
     * Update shader uniforms from current config.
     */
    fun updateUniforms() {
        if (::runtimeShader.isInitialized) {
            runtimeShader.setFloatUniform("contrast", config.contrast)
            runtimeShader.setFloatUniform("grainIntensity", config.grainIntensity)
            runtimeShader.setFloatUniform("inkColor", config.inkColor[0], config.inkColor[1], config.inkColor[2])
            runtimeShader.setFloatUniform("paperColor", config.paperColor[0], config.paperColor[1], config.paperColor[2])
        }
    }

    /**
     * Apply the filter to a captured frame.
     * Runs on IO dispatcher to avoid blocking main thread.
     */
    suspend fun apply(frame: CapturedFrame): FilterResult = withContext(Dispatchers.IO) {
        try {
            if (frame.bitmap.isRecycled) {
                return@withContext FilterResult.Error("Input bitmap already recycled")
            }

            // Create input shader from captured frame
            val inputShader = BitmapShader(
                frame.bitmap,
                Shader.TileMode.CLAMP,
                Shader.TileMode.CLAMP
            )

            // Create grain shader
            val grainShader = BitmapShader(
                grainBitmap,
                Shader.TileMode.REPEAT,
                Shader.TileMode.REPEAT
            )

            // Set input shaders
            runtimeShader.setInputShader("inputImage", inputShader)
            runtimeShader.setInputShader("grainTexture", grainShader)
            runtimeShader.setFloatUniform("resolution", frame.width.toFloat(), frame.height.toFloat())

            // Create output bitmap
            val output = Bitmap.createBitmap(frame.width, frame.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(output)
            
            val paint = Paint().apply {
                this.shader = runtimeShader
            }

            canvas.drawRect(0f, 0f, frame.width.toFloat(), frame.height.toFloat(), paint)

            // Cleanup input shaders
            inputShader.invalidateShader()
            grainShader.invalidateShader()

            FilterResult.Success(output)
        } catch (e: Exception) {
            FilterResult.Error("Failed to apply filter", e)
        }
    }

    /**
     * Create a RenderEffect for direct Canvas application (alternative to bitmap processing).
     * More efficient but requires API 31+ for RenderEffect support.
     */
    @RequiresApi(Build.VERSION_CODES.S)
    fun createRenderEffect(): RenderEffect? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            updateUniforms()
            RenderEffect.createRuntimeShaderEffect(runtimeShader, "inputImage")
        } else {
            null // Fallback handled elsewhere
        }
    }

    /**
     * Release resources.
     */
    fun release() {
        if (!grainBitmap.isRecycled) {
            grainBitmap.recycle()
        }
    }
}

/**
 * Fallback filter implementation for API <33 using CPU-based ColorMatrix.
 * Only applies grayscale + contrast, no grain or dithering.
 */
class LegacyPaperFilter(private val config: FilterConfig) {

    /**
     * Apply legacy filter using ColorMatrix operations.
     */
    suspend fun apply(frame: CapturedFrame): FilterResult = withContext(Dispatchers.IO) {
        try {
            if (frame.bitmap.isRecycled) {
                return@withContext FilterResult.Error("Input bitmap already recycled")
            }

            val output = frame.bitmap.copy(Bitmap.Config.ARGB_8888, false)
            val canvas = Canvas(output)
            val paint = Paint()

            // Grayscale + contrast matrix
            val contrast = config.contrast
            val scale = (100 + contrast * 100) / 100f
            val translate = ((1 - scale) * 128).toInt()

            val colorMatrix = android.graphics.ColorMatrix().apply {
                // First apply grayscale
                setSaturation(0f)
                
                // Then apply contrast
                val postMatrix = android.graphics.ColorMatrix().apply {
                    set(floatArrayOf(
                        scale, 0f, 0f, 0f, translate.toFloat(),
                        0f, scale, 0f, 0f, translate.toFloat(),
                        0f, 0f, scale, 0f, translate.toFloat(),
                        0f, 0f, 0f, 1f, 0f
                    ))
                }
                postConcat(postMatrix)
            }

            paint.colorFilter = android.graphics.ColorMatrixColorFilter(colorMatrix)
            canvas.drawBitmap(output, 0f, 0f, paint)

            FilterResult.Success(output)
        } catch (e: Exception) {
            FilterResult.Error("Failed to apply legacy filter", e)
        }
    }
}
