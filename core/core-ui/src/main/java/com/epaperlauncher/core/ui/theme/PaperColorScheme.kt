package com.epaperlauncher.core.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isSpecified
import com.epaperlauncher.core.ui.PaperTokens

/**
 * Paper Color Scheme
 * Maps design tokens to a cohesive color scheme for light and dark modes
 * Kindle-inspired aesthetic: warm cream backgrounds, flat grayscale ink tones
 */
data class PaperColorScheme(
    // Backgrounds
    val background: Color = PaperTokens.paperBackground,
    val surface: Color = PaperTokens.paperSurface,
    val surfaceVariant: Color = PaperTokens.paperSurfaceVariant,
    
    // Ink (Text) Colors
    val onBackground: Color = PaperTokens.inkPrimary,
    val onSurface: Color = PaperTokens.inkPrimary,
    val onSurfaceVariant: Color = PaperTokens.inkSecondary,
    
    // Dividers
    val outline: Color = PaperTokens.divider,
    val outlineVariant: Color = PaperTokens.dividerSubtle,
    
    // States
    val selected: Color = PaperTokens.stateSelected,
    val pressed: Color = PaperTokens.statePressed,
    val focus: Color = PaperTokens.stateFocus,
    val error: Color = PaperTokens.stateError,
    val success: Color = PaperTokens.stateSuccess,
    
    // Dark mode variants
    val darkBackground: Color = PaperTokens.darkBackground,
    val darkSurface: Color = PaperTokens.darkSurface,
    val darkSurfaceVariant: Color = PaperTokens.darkSurfaceVariant,
    val darkOnBackground: Color = PaperTokens.darkInkPrimary,
    val darkOnSurface: Color = PaperTokens.darkInkPrimary,
    val darkOnSurfaceVariant: Color = PaperTokens.darkInkSecondary,
    val darkOutline: Color = PaperTokens.darkDivider,
    val darkOutlineVariant: Color = PaperTokens.darkDividerSubtle,
) {
    companion object {
        val Light = PaperColorScheme()
        
        val Dark = PaperColorScheme(
            background = PaperTokens.darkBackground,
            surface = PaperTokens.darkSurface,
            surfaceVariant = PaperTokens.darkSurfaceVariant,
            onBackground = PaperTokens.darkInkPrimary,
            onSurface = PaperTokens.darkInkPrimary,
            onSurfaceVariant = PaperTokens.darkInkSecondary,
            outline = PaperTokens.darkDivider,
            outlineVariant = PaperTokens.darkDividerSubtle,
        )
    }
}
