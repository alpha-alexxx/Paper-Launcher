package com.epaperlauncher.iconengine.data.repository

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import androidx.core.graphics.createBitmap
import com.epaperlauncher.core.common.dispatcher.Dispatcher
import com.epaperlauncher.core.common.dispatcher.EPaperDispatchers
import com.epaperlauncher.core.data.local.IconCacheDao
import com.epaperlauncher.core.data.model.IconCacheEntity
import com.epaperlauncher.iconengine.domain.model.CURRENT_ALGORITHM_VERSION
import com.epaperlauncher.iconengine.domain.model.IconProcessingResult
import com.epaperlauncher.iconengine.domain.model.IconStyle
import com.epaperlauncher.iconengine.domain.repository.IconRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IconRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val iconCacheDao: IconCacheDao,
    private val packageManager: PackageManager,
    @Dispatcher(EPaperDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    @Dispatcher(EPaperDispatchers.Default) private val defaultDispatcher: CoroutineDispatcher
) : IconRepository {

    private val iconsDir: File by lazy {
        File(context.filesDir, "icons").apply { 
            if (!exists()) mkdirs() 
        }
    }

    override fun observeIconPath(packageName: String): Flow<String?> =
        iconCacheDao.observeIconPath(packageName).map { cacheEntry ->
            cacheEntry?.filePath?.takeIf { File(it).exists() }
        }

    override suspend fun getIconPath(packageName: String): String? = withContext(ioDispatcher) {
        iconCacheDao.getIconCache(packageName)?.filePath?.takeIf { File(it).exists() }
    }

    override suspend fun requestProcessing(
        packageName: String,
        style: IconStyle,
        forceRegenerate: Boolean
    ): IconProcessingResult = withContext(ioDispatcher) {
        try {
            // Check existing cache
            val existingCache = iconCacheDao.getIconCache(packageName)
            
            if (!forceRegenerate && existingCache != null) {
                val cachedFile = File(existingCache.filePath)
                if (cachedFile.exists() && 
                    existingCache.algorithmVersion == CURRENT_ALGORITHM_VERSION) {
                    return@withContext IconProcessingResult.Skipped
                }
            }

            // Get app icon from PackageManager
            val appInfo = packageManager.getApplicationInfo(packageName, 0)
            val drawable = packageManager.getApplicationIcon(appInfo)
            
            // Convert drawable to Bitmap
            val bitmap = drawableToBitmap(drawable) ?: run {
                return@withContext IconProcessingResult.Error("Failed to load icon for $packageName")
            }

            // Process icon based on style
            val processedBitmap = when (style) {
                IconStyle.FLAT_MONOCHROME -> processFlatMonochrome(bitmap)
                IconStyle.LINE_ART -> processLineArt(bitmap)
                IconStyle.STAMP_STYLE -> processStampStyle(bitmap)
            }

            // Save processed icon
            val outputFile = File(iconsDir, "$packageName.png")
            FileOutputStream(outputFile).use { fos ->
                processedBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
            }

            // Update cache
            val cacheEntity = IconCacheEntity(
                packageName = packageName,
                filePath = outputFile.absolutePath,
                processedVersionCode = appInfo.sourceDir.hashCode(), // Simple version tracking
                algorithmVersion = CURRENT_ALGORITHM_VERSION
            )
            iconCacheDao.insertOrUpdate(cacheEntity)

            IconProcessingResult.Success(outputFile.absolutePath)
        } catch (e: Exception) {
            IconProcessingResult.Error("Processing failed for $packageName", e)
        }
    }

    override suspend fun invalidateCache(packageName: String) {
        withContext(ioDispatcher) {
            iconCacheDao.deleteIconCache(packageName)
            File(iconsDir, "$packageName.png").delete()
        }
    }

    override suspend fun clearAllCache() {
        withContext(ioDispatcher) {
            iconCacheDao.clearAllCache()
            iconsDir.deleteRecursively()
            iconsDir.mkdirs()
        }
    }

    /**
     * Convert any Drawable type to Bitmap
     */
    private fun drawableToBitmap(drawable: Drawable): Bitmap? {
        return when (drawable) {
            is BitmapDrawable -> drawable.bitmap
            
            is AdaptiveIconDrawable -> {
                // Handle adaptive icons by drawing foreground on background
                val size = maxOf(drawable.intrinsicWidth, drawable.intrinsicHeight)
                if (size <= 0) return null
                
                val bitmap = createBitmap(size, size, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(bitmap)
                
                // Draw background layer first
                drawable.background?.let { bg ->
                    bg.setBounds(0, 0, size, size)
                    bg.draw(canvas)
                }
                
                // Draw foreground layer
                drawable.foreground?.let { fg ->
                    fg.setBounds(0, 0, size, size)
                    fg.draw(canvas)
                }
                
                bitmap
            }
            
            is LayerDrawable -> {
                // Handle layered drawables
                val size = maxOf(drawable.intrinsicWidth, drawable.intrinsicHeight)
                if (size <= 0) return null
                
                val bitmap = createBitmap(size, size, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(bitmap)
                drawable.setBounds(0, 0, size, size)
                drawable.draw(canvas)
                bitmap
            }
            
            else -> {
                // Generic drawable handling
                val width = drawable.intrinsicWidth.takeIf { it > 0 } ?: 192
                val height = drawable.intrinsicHeight.takeIf { it > 0 } ?: 192
                
                val bitmap = createBitmap(width, height, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(bitmap)
                drawable.setBounds(0, 0, width, height)
                drawable.draw(canvas)
                bitmap
            }
        }
    }

    /**
     * Style 1: Flat monochrome - grayscale + posterize
     */
    private fun processFlatMonochrome(bitmap: Bitmap): Bitmap {
        val result = createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(result)
        
        // Grayscale conversion
        val colorMatrix = ColorMatrix().apply {
            setSaturation(0f) // Remove all color
        }
        
        val paint = Paint().apply {
            colorFilter = ColorMatrixColorFilter(colorMatrix)
        }
        
        canvas.drawBitmap(bitmap, 0f, 0f, paint)
        
        // Apply posterization (reduce tonal levels) via contrast boost
        val contrastMatrix = ColorMatrix().apply {
            val contrast = 1.3f // Boost contrast for paper-like appearance
            val translate = (1f - contrast) / 2f
            val matrix = floatArrayOf(
                contrast, 0f, 0f, 0f, translate,
                0f, contrast, 0f, 0f, translate,
                0f, 0f, contrast, 0f, translate,
                0f, 0f, 0f, 1f, 0f
            )
            set(matrix)
        }
        
        paint.colorFilter = ColorMatrixColorFilter(contrastMatrix)
        canvas.drawBitmap(result, 0f, 0f, paint)
        
        return result
    }

    /**
     * Style 2: Line art - edge detection heavy
     */
    private fun processLineArt(bitmap: Bitmap): Bitmap {
        // Start with grayscale
        val grayscale = processFlatMonochrome(bitmap)
        
        // Simple edge detection using Sobel-like filter
        val width = grayscale.width
        val height = grayscale.height
        val result = createBitmap(width, height, Bitmap.Config.ARGB_8888)
        
        val pixels = IntArray(width * height)
        grayscale.getPixels(pixels, 0, width, 0, 0, width, height)
        
        val outputPixels = IntArray(width * height)
        
        // Simple edge detection kernel
        for (y in 1 until height - 1) {
            for (x in 1 until width - 1) {
                val idx = y * width + x
                
                // Get neighboring pixels' luminance
                val left = pixels[idx - 1] and 0xFF
                val right = pixels[idx + 1] and 0xFF
                val top = pixels[idx - width] and 0xFF
                val bottom = pixels[idx + width] and 0xFF
                
                // Edge strength
                val edgeX = (right - left).coerceIn(-255, 255)
                val edgeY = (bottom - top).coerceIn(-255, 255)
                val edgeStrength = kotlin.math.sqrt((edgeX * edgeX + edgeY * edgeY).toDouble())
                
                // Invert: strong edges become dark lines
                val intensity = (255 - edgeStrength.coerceAtMost(255.0)).toInt()
                outputPixels[idx] = 0xFF000000.toInt() or (intensity shl 16) or (intensity shl 8) or intensity
            }
        }
        
        result.setPixels(outputPixels, 0, width, 0, 0, width, height)
        grayscale.recycle()
        
        return result
    }

    /**
     * Style 3: Stamp style - circular clip with border
     */
    private fun processStampStyle(bitmap: Bitmap): Bitmap {
        val size = minOf(bitmap.width, bitmap.height)
        val result = createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(result)
        
        // Clear with transparent background
        canvas.drawColor(0x00000000)
        
        // Draw circular clipped grayscale icon
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            colorFilter = ColorMatrixColorFilter(ColorMatrix().apply { setSaturation(0f) })
        }
        
        // Create circular path
        val path = android.graphics.Path().apply {
            addCircle(size / 2f, size / 2f, size / 2f - 2f, android.graphics.Path.Direction.CW)
        }
        
        canvas.clipPath(path)
        canvas.drawBitmap(bitmap, 0f, 0f, paint)
        
        // Add border stroke
        val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFF2B2A28.toInt() // Ink color
            style = Paint.Style.STROKE
            strokeWidth = 4f
        }
        
        canvas.drawCircle(size / 2f, size / 2f, size / 2f - 2f, borderPaint)
        
        return result
    }
}
