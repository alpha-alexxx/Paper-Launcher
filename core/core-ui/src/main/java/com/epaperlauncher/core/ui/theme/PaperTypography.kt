package com.epaperlauncher.core.ui.theme

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.epaperlauncher.core.ui.PaperTokens

/**
 * Typography system for E-Paper Launcher
 * Clean sans-serif font throughout for a professional, minimalist aesthetic
 * Kindle-inspired: high readability, clear hierarchy, no decorative fonts
 */

// Use system sans-serif (Inter/System UI) for clean, modern readability
val DefaultFontFamily = FontFamily.SansSerif

data class PaperTypography(
    // Display - Large headlines
    val displayLarge: TextStyle = TextStyle(
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = (-0.5).sp
    ),
    
    // Title - Section headers
    val titleLarge: TextStyle = TextStyle(
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    
    val titleMedium: TextStyle = TextStyle(
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp
    ),
    
    val titleSmall: TextStyle = TextStyle(
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 15.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    
    // Body - Main content text
    val bodyLarge: TextStyle = TextStyle(
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 17.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp
    ),
    
    val bodyMedium: TextStyle = TextStyle(
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.2.sp
    ),
    
    val bodySmall: TextStyle = TextStyle(
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.2.sp
    ),
    
    // Label - UI elements, buttons, captions
    val labelLarge: TextStyle = TextStyle(
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    
    val labelMedium: TextStyle = TextStyle(
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    
    val labelSmall: TextStyle = TextStyle(
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
) {
    companion object {
        val Default = PaperTypography()
    }
}

/**
 * CompositionLocal to provide typography across the app
 */
val LocalPaperTypography = compositionLocalOf { PaperTypography.Default }
