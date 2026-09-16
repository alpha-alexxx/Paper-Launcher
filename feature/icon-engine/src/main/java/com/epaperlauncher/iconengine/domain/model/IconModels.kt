package com.epaperlauncher.iconengine.domain.model

/**
 * Icon processing style variants
 */
enum class IconStyle {
    /** Grayscale + posterize only (default) */
    FLAT_MONOCHROME,
    
    /** Edge-detection heavy, minimal fill */
    LINE_ART,
    
    /** Circular clip + border, ink-stamp aesthetic */
    STAMP_STYLE
}

/**
 * Result of icon processing operation
 */
sealed class IconProcessingResult {
    data class Success(val filePath: String) : IconProcessingResult()
    data class Error(val message: String, val exception: Throwable? = null) : IconProcessingResult()
    object Skipped : IconProcessingResult() // Already cached with same algorithm version
}

/**
 * Current algorithm version - bump when icon processing logic changes
 * This invalidates existing caches and triggers re-processing
 */
const val CURRENT_ALGORITHM_VERSION = 1
