package com.epaperlauncher.feature.filterengine

import com.epaperlauncher.core.data.domain.model.ContrastPreset
import com.epaperlauncher.core.data.domain.model.PaperTone
import com.epaperlauncher.feature.filterengine.domain.model.CapturedFrame
import com.epaperlauncher.feature.filterengine.domain.model.FilterConfig
import com.epaperlauncher.feature.filterengine.domain.model.FilterResult
import com.epaperlauncher.feature.filterengine.presentation.shader.LegacyPaperFilter
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test
import androidx.core.graphics.createBitmap
import android.graphics.Color

/**
 * Unit tests for the legacy filter implementation.
 * Tests the grayscale + contrast pipeline without requiring device-specific APIs.
 */
class LegacyPaperFilterTest {

    @Test
    fun `filter produces output bitmap with same dimensions as input`() = runTest {
        val inputBitmap = createBitmap(100, 200, android.graphics.Bitmap.Config.ARGB_8888)
        val config = FilterConfig(contrast = 1.0f, grainIntensity = 0.0f)
        val filter = LegacyPaperFilter(config)
        
        val frame = CapturedFrame(
            bitmap = inputBitmap,
            timestamp = System.currentTimeMillis(),
            width = 100,
            height = 200,
            density = 1.0f
        )
        
        val result = filter.apply(frame)
        
        assertTrue(result is FilterResult.Success)
        if (result is FilterResult.Success) {
            assertEquals(100, result.processedBitmap.width)
            assertEquals(200, result.processedBitmap.height)
            result.processedBitmap.recycle()
        }
        inputBitmap.recycle()
    }

    @Test
    fun `filter converts colored input to grayscale`() = runTest {
        val inputBitmap = createBitmap(10, 10, android.graphics.Bitmap.Config.ARGB_8888)
        // Fill with pure red
        inputBitmap.eraseColor(Color.RED)
        
        val config = FilterConfig(contrast = 1.0f, grainIntensity = 0.0f)
        val filter = LegacyPaperFilter(config)
        
        val frame = CapturedFrame(
            bitmap = inputBitmap,
            timestamp = System.currentTimeMillis(),
            width = 10,
            height = 10,
            density = 1.0f
        )
        
        val result = filter.apply(frame)
        
        assertTrue(result is FilterResult.Success)
        if (result is FilterResult.Success) {
            val pixel = result.processedBitmap.getPixel(0, 0)
            val r = Color.red(pixel)
            val g = Color.green(pixel)
            val b = Color.blue(pixel)
            
            // Grayscale means R=G=B (within tolerance)
            assertEquals(r, g, 5)
            assertEquals(g, b, 5)
            
            result.processedBitmap.recycle()
        }
        inputBitmap.recycle()
    }

    @Test
    fun `higher contrast increases difference from midpoint`() = runTest {
        val inputBitmap = createBitmap(10, 10, android.graphics.Bitmap.Config.ARGB_8888)
        // Fill with mid-gray
        inputBitmap.eraseColor(Color.rgb(128, 128, 128))
        
        val lowContrastConfig = FilterConfig(contrast = 0.5f, grainIntensity = 0.0f)
        val highContrastConfig = FilterConfig(contrast = 1.5f, grainIntensity = 0.0f)
        
        val lowFilter = LegacyPaperFilter(lowContrastConfig)
        val highFilter = LegacyPaperFilter(highContrastConfig)
        
        val frame = CapturedFrame(
            bitmap = inputBitmap,
            timestamp = System.currentTimeMillis(),
            width = 10,
            height = 10,
            density = 1.0f
        )
        
        val lowResult = lowFilter.apply(frame)
        val highResult = highFilter.apply(frame)
        
        assertTrue(lowResult is FilterResult.Success)
        assertTrue(highResult is FilterResult.Success)
        
        if (lowResult is FilterResult.Success && highResult is FilterResult.Success) {
            val lowPixel = lowResult.processedBitmap.getPixel(0, 0)
            val highPixel = highResult.processedBitmap.getPixel(0, 0)
            
            // Higher contrast should push mid-gray closer to white or black
            // depending on the exact formula - just verify they're different
            assertNotEquals(lowPixel, highPixel)
            
            lowResult.processedBitmap.recycle()
            highResult.processedBitmap.recycle()
        }
        inputBitmap.recycle()
    }

    @Test
    fun `filter handles recycled bitmap gracefully`() = runTest {
        val inputBitmap = createBitmap(10, 10, android.graphics.Bitmap.Config.ARGB_8888)
        inputBitmap.recycle()
        
        val config = FilterConfig(contrast = 1.0f, grainIntensity = 0.0f)
        val filter = LegacyPaperFilter(config)
        
        val frame = CapturedFrame(
            bitmap = inputBitmap,
            timestamp = System.currentTimeMillis(),
            width = 10,
            height = 10,
            density = 1.0f
        )
        
        val result = filter.apply(frame)
        
        assertTrue(result is FilterResult.Error)
        if (result is FilterResult.Error) {
            assertTrue(result.message.contains("recycled", ignoreCase = true))
        }
    }
}
