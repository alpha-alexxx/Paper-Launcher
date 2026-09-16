package com.epaperlauncher.feature.settings

import com.epaperlauncher.core.data.domain.model.ContrastPreset
import com.epaperlauncher.core.data.domain.model.FilterEngineState
import com.epaperlauncher.core.data.domain.model.PaperTone
import com.epaperlauncher.core.data.domain.model.ThemeConfig
import com.epaperlauncher.core.data.domain.repository.BillingRepository
import com.epaperlauncher.core.data.domain.repository.FilterEngineController
import com.epaperlauncher.core.data.domain.repository.IconRepository
import com.epaperlauncher.core.data.domain.repository.ThemeRepository
import com.epaperlauncher.feature.settings.presentation.SettingsUiState
import com.epaperlauncher.feature.settings.presentation.SettingsViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

/**
 * Unit tests for SettingsViewModel.
 */
class SettingsViewModelTest {

    private lateinit var themeRepository: ThemeRepository
    private lateinit var filterEngineController: FilterEngineController
    private lateinit var iconRepository: IconRepository
    private lateinit var billingRepository: BillingRepository
    private lateinit var viewModel: SettingsViewModel

    @Before
    fun setup() {
        themeRepository = mock()
        filterEngineController = mock()
        iconRepository = mock()
        billingRepository = mock()

        // Setup default state flows
        whenever(themeRepository.observeThemeConfig()).thenReturn(
            MutableStateFlow(ThemeConfig())
        )
        whenever(filterEngineController.observeEngineState()).thenReturn(
            MutableStateFlow(FilterEngineState.DISABLED)
        )
        whenever(billingRepository.observeEntitlement()).thenReturn(
            MutableStateFlow(false)
        )
        whenever(filterEngineController.hasOverlayPermission()).thenReturn(true)
        whenever(filterEngineController.isAccessibilityServiceEnabled()).thenReturn(false)

        viewModel = SettingsViewModel(
            themeRepository = themeRepository,
            filterEngineController = filterEngineController,
            iconRepository = iconRepository,
            billingRepository = billingRepository
        )
    }

    @Test
    fun `initial state has correct defaults`() {
        val state = viewModel.uiState.value

        assertEquals(PaperTone.WARM, state.themeConfig.paperTone)
        assertEquals(0.1f, state.themeConfig.grainIntensity, 0.01f)
        assertEquals(ContrastPreset.STANDARD, state.themeConfig.contrastCurve)
        assertFalse(state.themeConfig.filterEnabledSystemWide)
        assertFalse(state.isProUnlocked)
        assertFalse(state.isLoading)
    }

    @Test
    fun `setPaperTone updates theme config`() = runTest {
        viewModel.setPaperTone(PaperTone.COLD)

        verify(themeRepository).update(any())
    }

    @Test
    fun `setGrainIntensity clamps value to valid range`() = runTest {
        // Test upper bound
        viewModel.setGrainIntensity(0.5f)
        verify(themeRepository).update { 
            it.copy(grainIntensity = 0.3f) 
        }

        // Test lower bound
        viewModel.setGrainIntensity(-0.1f)
        verify(themeRepository).update { 
            it.copy(grainIntensity = 0f) 
        }
    }

    @Test
    fun `togglePageTurnAnimation flips the value`() = runTest {
        // Initial state is true (default)
        viewModel.togglePageTurnAnimation()

        verify(themeRepository).update {
            it.copy(pageTurnAnimation = false)
        }
    }

    @Test
    fun `toggleAutoDayNight flips the value`() = runTest {
        // Initial state is true (default)
        viewModel.toggleAutoDayNight()

        verify(themeRepository).update {
            it.copy(autoDayNightSwitch = false)
        }
    }

    @Test
    fun `toggleSystemWideFilter enables filter when disabled`() = runTest {
        whenever(filterEngineController.requestEnable()).thenReturn(FilterEngineState.ACTIVE)

        viewModel.toggleSystemWideFilter()

        verify(filterEngineController).requestEnable()
        verify(themeRepository).update(any())
    }

    @Test
    fun `toggleSystemWideFilter disables filter when enabled`() = runTest {
        // Setup initial state with filter enabled
        whenever(themeRepository.observeThemeConfig()).thenReturn(
            MutableStateFlow(ThemeConfig(filterEnabledSystemWide = true))
        )
        // Recreate viewModel with new mock behavior
        viewModel = SettingsViewModel(
            themeRepository = themeRepository,
            filterEngineController = filterEngineController,
            iconRepository = iconRepository,
            billingRepository = billingRepository
        )

        viewModel.toggleSystemWideFilter()

        verify(filterEngineController).disable()
        verify(themeRepository).update {
            it.copy(filterEnabledSystemWide = false)
        }
    }

    @Test
    fun `setPerAppOverride adds override to map`() = runTest {
        val packageName = "com.example.app"
        
        viewModel.setPerAppOverride(packageName, true)

        verify(themeRepository).update {
            it.copy(
                perAppFilterOverrides = mapOf(packageName to true)
            )
        }
        verify(filterEngineController).setPerAppOverride(packageName, true)
    }
}
