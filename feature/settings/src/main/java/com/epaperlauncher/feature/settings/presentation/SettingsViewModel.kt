package com.epaperlauncher.feature.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.epaperlauncher.core.data.domain.model.FilterEngineState
import com.epaperlauncher.core.data.domain.model.IconStyle
import com.epaperlauncher.core.data.domain.model.PaperTone
import com.epaperlauncher.core.data.domain.model.ThemeConfig
import com.epaperlauncher.core.data.domain.repository.BillingRepository
import com.epaperlauncher.core.data.domain.repository.FilterEngineController
import com.epaperlauncher.core.data.domain.repository.IconRepository
import com.epaperlauncher.core.data.domain.repository.ThemeRepository
import com.epaperlauncher.feature.billing.domain.model.EntitlementState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI state for the settings screen.
 */
data class SettingsUiState(
    val themeConfig: ThemeConfig = ThemeConfig(),
    val filterEngineState: FilterEngineState = FilterEngineState.DISABLED,
    val isProUnlocked: Boolean = false,
    val isLoading: Boolean = true,
    val hasOverlayPermission: Boolean = false,
    val isAccessibilityServiceEnabled: Boolean = false
)

/**
 * ViewModel for the settings screen.
 * Manages theme configuration, filter engine state, and billing entitlement.
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val themeRepository: ThemeRepository,
    private val filterEngineController: FilterEngineController,
    private val iconRepository: IconRepository,
    private val billingRepository: BillingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        observeAll()
    }

    private fun observeAll() {
        viewModelScope.launch {
            combine(
                themeRepository.observeThemeConfig(),
                filterEngineController.observeEngineState(),
                billingRepository.observeEntitlement()
            ) { themeConfig, filterState, entitlement ->
                SettingsUiState(
                    themeConfig = themeConfig,
                    filterEngineState = filterState,
                    isProUnlocked = entitlement is EntitlementState.ProUnlocked,
                    isLoading = false,
                    hasOverlayPermission = filterEngineController.hasOverlayPermission(),
                    isAccessibilityServiceEnabled = filterEngineController.isAccessibilityServiceEnabled()
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    /**
     * Update paper tone.
     */
    fun setPaperTone(tone: PaperTone) {
        viewModelScope.launch {
            themeRepository.update { it.copy(paperTone = tone) }
        }
    }

    /**
     * Update grain intensity.
     */
    fun setGrainIntensity(intensity: Float) {
        viewModelScope.launch {
            themeRepository.update { it.copy(grainIntensity = intensity.coerceIn(0f, 0.3f)) }
        }
    }

    /**
     * Update contrast preset.
     */
    fun setContrastPreset(preset: com.epaperlauncher.core.data.domain.model.ContrastPreset) {
        viewModelScope.launch {
            themeRepository.update { it.copy(contrastCurve = preset) }
        }
    }

    /**
     * Toggle page turn animation.
     */
    fun togglePageTurnAnimation() {
        viewModelScope.launch {
            themeRepository.update { it.copy(pageTurnAnimation = !it.pageTurnAnimation) }
        }
    }

    /**
     * Toggle auto day/night switch.
     */
    fun toggleAutoDayNight() {
        viewModelScope.launch {
            themeRepository.update { it.copy(autoDayNightSwitch = !it.autoDayNightSwitch) }
        }
    }

    /**
     * Update icon style and request reprocessing of all icons.
     */
    fun setIconStyle(style: IconStyle) {
        viewModelScope.launch {
            themeRepository.update { it.copy(iconStyle = style) }
            // TODO: Request reprocessing of all icons with new style
        }
    }

    /**
     * Enable or disable system-wide filter.
     */
    fun toggleSystemWideFilter() {
        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState.themeConfig.filterEnabledSystemWide) {
                filterEngineController.disable()
                themeRepository.update { it.copy(filterEnabledSystemWide = false) }
            } else {
                val result = filterEngineController.requestEnable()
                themeRepository.update { 
                    it.copy(
                        filterEnabledSystemWide = true,
                        perAppFilterOverrides = it.perAppFilterOverrides
                    ) 
                }
            }
        }
    }

    /**
     * Set per-app filter override.
     */
    fun setPerAppOverride(packageName: String, enabled: Boolean) {
        viewModelScope.launch {
            themeRepository.update { 
                it.copy(
                    perAppFilterOverrides = it.perAppFilterOverrides + (packageName to enabled)
                ) 
            }
            filterEngineController.setPerAppOverride(packageName, enabled)
        }
    }

    /**
     * Launch purchase flow for Pro tier.
     */
    fun launchPurchaseFlow(activity: android.app.Activity) {
        viewModelScope.launch {
            billingRepository.launchPurchaseFlow(activity)
        }
    }

    /**
     * Restore purchases.
     */
    fun restorePurchases() {
        viewModelScope.launch {
            billingRepository.restorePurchases()
        }
    }
}
