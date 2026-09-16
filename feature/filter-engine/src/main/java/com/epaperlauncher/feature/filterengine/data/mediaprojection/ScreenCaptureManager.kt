package com.epaperlauncher.feature.filterengine.data.mediaprojection

import android.content.Context
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.Image
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.view.Surface
import com.epaperlauncher.feature.filterengine.domain.model.CapturedFrame
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import kotlin.concurrent.thread

/**
 * Manages MediaProjection screen capture pipeline.
 * Creates a VirtualDisplay that mirrors the screen and captures frames via ImageReader.
 */
class ScreenCaptureManager(
    private val context: Context,
    private val mediaProjection: MediaProjection
) {
    
    private var virtualDisplay: VirtualDisplay? = null
    private var imageReader: ImageReader? = null
    private var isCapturing = false
    
    private val _latestFrame = MutableStateFlow<CapturedFrame?>(null)
    val latestFrame: StateFlow<CapturedFrame?> = _latestFrame.asStateFlow()
    
    private val displayMetrics = DisplayMetrics()
    private val handler = Handler(Looper.getMainLooper())
    
    companion object {
        private const val TAG = "ScreenCaptureManager"
    }
    
    /**
     * Start capturing screen frames.
     * @param densityDpi The DPI for the virtual display
     * @param frameCaptureCallback Called when a new frame is available
     */
    fun startCapture(
        densityDpi: Int? = null,
        frameCaptureCallback: ((CapturedFrame) -> Unit)? = null
    ) {
        if (isCapturing) return
        
        // Get display metrics
        val displayManager = context.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
        val display = displayManager.getDisplay(DisplayManager.DEFAULT_DISPLAY)
        display.getMetrics(displayMetrics)
        
        val width = displayMetrics.widthPixels
        val height = displayMetrics.heightPixels
        val dpi = densityDpi ?: displayMetrics.densityDpi
        
        // Create ImageReader for frame capture
        imageReader = ImageReader.newInstance(width, height, android.graphics.PixelFormat.RGBA_8888, 2)
        
        imageReader?.setOnImageAvailableListener({ reader ->
            val image: Image? = reader.acquireLatestImage()
            image?.let {
                val bitmap = image.toBitmap()
                if (!bitmap.isRecycled) {
                    val frame = CapturedFrame(
                        bitmap = bitmap,
                        timestamp = System.currentTimeMillis(),
                        width = width,
                        height = height,
                        density = displayMetrics.density
                    )
                    _latestFrame.value = frame
                    frameCaptureCallback?.invoke(frame)
                }
                image.close()
            }
        }, handler)
        
        // Create virtual display
        virtualDisplay = mediaProjection.createVirtualDisplay(
            "E-Paper-Launcher-Capture",
            width,
            height,
            dpi,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            imageReader?.surface,
            null,
            handler
        )
        
        isCapturing = true
    }
    
    /**
     * Stop screen capture and release resources.
     */
    fun stopCapture() {
        virtualDisplay?.release()
        virtualDisplay = null
        
        imageReader?.close()
        imageReader = null
        
        isCapturing = false
    }
    
    /**
     * Release all resources including MediaProjection.
     */
    fun release() {
        stopCapture()
        mediaProjection.stop()
    }
}

/**
 * Extension to convert Android Media Image to Bitmap.
 */
private fun Image.toBitmap(): android.graphics.Bitmap {
    val plane = planes[0]
    val buffer = plane.buffer
    val pixelStride = plane.pixelStride
    val rowStride = plane.rowStride
    val rowPadding = rowStride - pixelStride * width
    
    val bitmap = android.graphics.Bitmap.createBitmap(
        width + rowPadding / pixelStride,
        height,
        android.graphics.Bitmap.Config.ARGB_8888
    )
    
    bitmap.copyPixelsFromBuffer(buffer)
    buffer.rewind()
    
    // Crop to exact dimensions if there was padding
    return if (rowPadding > 0) {
        val cropped = android.graphics.Bitmap.createBitmap(bitmap, 0, 0, width, height)
        bitmap.recycle()
        cropped
    } else {
        bitmap
    }
}

/**
 * Factory for creating ScreenCaptureManager instances.
 * Handles the MediaProjection permission result callback.
 */
class MediaProjectionFactory(private val context: Context) {
    
    private var mediaProjectionManager: android.media.projection.MediaProjectionManager? = null
    
    init {
        mediaProjectionManager = 
            context.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as? android.media.projection.MediaProjectionManager
    }
    
    /**
     * Get the intent for requesting MediaProjection permission.
     * Call this before starting capture to show the system dialog.
     */
    fun getCapturePermissionIntent(): android.content.Intent {
        return mediaProjectionManager?.createScreenCaptureIntent()
            ?: throw IllegalStateException("MediaProjectionManager not available")
    }
    
    /**
     * Create a MediaProjection from the result code and intent data.
     * Call this in onActivityResult after user grants permission.
     */
    fun createMediaProjection(resultCode: Int, data: android.content.Intent): MediaProjection? {
        return mediaProjectionManager?.getMediaProjection(resultCode, data)
    }
    
    /**
     * Create and start a ScreenCaptureManager after permission is granted.
     */
    suspend fun createScreenCaptureManager(
        resultCode: Int,
        data: android.content.Intent,
        frameCallback: ((CapturedFrame) -> Unit)? = null
    ): ScreenCaptureManager? = withContext(Dispatchers.IO) {
        val mediaProjection = createMediaProjection(resultCode, data) ?: return@withContext null
        
        val manager = ScreenCaptureManager(context, mediaProjection)
        manager.startCapture(frameCaptureCallback = frameCallback)
        manager
    }
}
