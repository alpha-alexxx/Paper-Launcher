package com.epaperlauncher.iconengine.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.epaperlauncher.iconengine.domain.model.IconStyle
import com.epaperlauncher.iconengine.domain.repository.IconRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/**
 * WorkManager worker for processing app icons asynchronously
 * Runs with low priority, respects Doze mode
 */
@HiltWorker
class IconProcessingWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val iconRepository: IconRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result = try {
        val packageName = inputData.getString(KEY_PACKAGE_NAME) ?: return Result.failure()
        val styleName = inputData.getString(KEY_ICON_STYLE) ?: IconStyle.FLAT_MONOCHROME.name
        val forceRegenerate = inputData.getBoolean(KEY_FORCE_REGENERATE, false)
        
        val style = IconStyle.valueOf(styleName)
        
        val result = iconRepository.requestProcessing(packageName, style, forceRegenerate)
        
        when (result) {
            is com.epaperlauncher.iconengine.domain.model.IconProcessingResult.Success -> {
                Result.success()
            }
            is com.epaperlauncher.iconengine.domain.model.IconProcessingResult.Skipped -> {
                Result.success() // Cache hit is still success
            }
            is com.epaperlauncher.iconengine.domain.model.IconProcessingResult.Error -> {
                if (runAttemptCount < MAX_RETRIES) {
                    Result.retry()
                } else {
                    Result.failure()
                }
            }
        }
    } catch (e: Exception) {
        if (runAttemptCount < MAX_RETRIES) {
            Result.retry()
        } else {
            Result.failure()
        }
    }

    companion object {
        const val KEY_PACKAGE_NAME = "packageName"
        const val KEY_ICON_STYLE = "iconStyle"
        const val KEY_FORCE_REGENERATE = "forceRegenerate"
        const val MAX_RETRIES = 3
    }
}
