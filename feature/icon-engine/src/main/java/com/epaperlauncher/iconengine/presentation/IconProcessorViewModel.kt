package com.epaperlauncher.iconengine.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.epaperlauncher.iconengine.domain.model.IconProcessingResult
import com.epaperlauncher.iconengine.domain.model.IconStyle
import com.epaperlauncher.iconengine.domain.repository.IconRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI state for icon processing
 */
data class IconProcessorUiState(
    val isProcessing: Boolean = false,
    val processedCount: Int = 0,
    val error: String? = null,
    val currentStyle: IconStyle = IconStyle.FLAT_MONOCHROME
)

/**
 * ViewModel for managing icon processing operations
 */
@HiltViewModel
class IconProcessorViewModel @Inject constructor(
    private val iconRepository: IconRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(IconProcessorUiState())
    val uiState: StateFlow<IconProcessorUiState> = _uiState.asStateFlow()

    /**
     * Request icon processing for a single app
     */
    fun processIcon(packageName: String, style: IconStyle = IconStyle.FLAT_MONOCHROME) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isProcessing = true, currentStyle = style)
            
            val result = iconRepository.requestProcessing(packageName, style, forceRegenerate = true)
            
            when (result) {
                is IconProcessingResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isProcessing = false,
                        processedCount = _uiState.value.processedCount + 1
                    )
                }
                is IconProcessingResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isProcessing = false,
                        error = result.message
                    )
                }
                is IconProcessingResult.Skipped -> {
                    _uiState.value = _uiState.value.copy(
                        isProcessing = false
                    )
                }
            }
        }
    }

    /**
     * Process icons for multiple apps (batch operation)
     * Caller should manage throttling to avoid overwhelming WorkManager
     */
    fun processIconsBatch(packageNames: List<String>, style: IconStyle = IconStyle.FLAT_MONOCHROME) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isProcessing = true, currentStyle = style)
            
            var successCount = 0
            var errorCount = 0
            
            packageNames.forEach { packageName ->
                val result = iconRepository.requestProcessing(packageName, style, forceRegenerate = false)
                when (result) {
                    is IconProcessingResult.Success -> successCount++
                    is IconProcessingResult.Error -> errorCount++
                    is IconProcessingResult.Skipped -> successCount++ // Cache hit counts as success
                }
            }
            
            _uiState.value = _uiState.value.copy(
                isProcessing = false,
                processedCount = _uiState.value.processedCount + successCount,
                error = if (errorCount > 0) "$errorCount icons failed to process" else null
            )
        }
    }

    /**
     * Clear all cached icons and reset state
     */
    fun clearCache() {
        viewModelScope.launch {
            iconRepository.clearAllCache()
            _uiState.value = IconProcessorUiState()
        }
    }

    /**
     * Change the current icon style preference
     */
    fun setIconStyle(style: IconStyle) {
        _uiState.value = _uiState.value.copy(currentStyle = style)
    }
}
