package com.epaperlauncher.core.data.domain.model

import kotlinx.serialization.Serializable

/**
 * Paper tone variants for the visual theme.
 */
@Serializable
enum class PaperTone {
    WARM,      // Cream/sepia paper (default)
    COLD,      // Bluish-gray paper
    PURE_WHITE // Max contrast, Kindle-like
}

/**
 * Contrast presets for the filter engine.
 */
@Serializable
enum class ContrastPreset(val multiplier: Float) {
    LOW(0.8f),
    STANDARD(1.0f),
    HIGH(1.3f),
    EXTRA_HIGH(1.6f)
}

/**
 * Icon style variants.
 */
@Serializable
enum class IconStyle {
    FLAT_MONOCHROME,  // Default: grayscale + posterize
    LINE_ART,         // Edge-detection heavy, minimal fill
    STAMP             // Circular clip + border, ink-stamp aesthetic
}

/**
 * Complete theme configuration persisted via DataStore.
 */
@Serializable
data class ThemeConfig(
    val paperTone: PaperTone = PaperTone.WARM,
    val grainIntensity: Float = 0.1f,          // 0.0–0.3
    val contrastCurve: ContrastPreset = ContrastPreset.STANDARD,
    val ditherEnabled: Boolean = false,         // Rooted tier only
    val pageTurnAnimation: Boolean = true,
    val autoDayNightSwitch: Boolean = true,
    val filterEnabledSystemWide: Boolean = false,  // Requires MediaProjection consent
    val perAppFilterOverrides: Map<String, Boolean> = emptyMap(),
    val iconStyle: IconStyle = IconStyle.FLAT_MONOCHROME
)

/**
 * Filter engine state exposed to UI.
 */
@Serializable
enum class FilterEngineState {
    DISABLED,
    PERMISSION_REQUIRED,
    ACTIVE,
    ERROR
}
