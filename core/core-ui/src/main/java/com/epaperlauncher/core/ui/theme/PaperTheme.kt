package com.epaperlauncher.core.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import com.epaperlauncher.core.ui.PaperColorScheme

/**
 * CompositionLocal to provide the current color scheme
 */
val LocalPaperColorScheme = compositionLocalOf { PaperColorScheme.Light }

/**
 * Access the current paper color scheme from anywhere in the composition
 */
object PaperTheme {
    val colors: PaperColorScheme
        @Composable
        @ReadOnlyComposable
        get() = LocalPaperColorScheme.current
    
    val typography
        @Composable
        @ReadOnlyComposable
        get() = LocalPaperTypography.current
}

/**
 * Main theme provider for the entire app
 * Wraps your content with the E-Paper design system
 */
@Composable
fun PaperTheme(
    darkMode: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkMode) PaperColorScheme.Dark else PaperColorScheme.Light
    
    CompositionLocalProvider(
        LocalPaperColorScheme provides colorScheme,
        LocalPaperTypography provides PaperTypography.Default,
        content = content
    )
}

/**
 * Theme preview helper for @Preview annotations
 */
@Composable
fun PreviewPaperTheme(
    darkMode: Boolean = false,
    content: @Composable () -> Unit
) {
    PaperTheme(darkMode = darkMode, content = content)
}
