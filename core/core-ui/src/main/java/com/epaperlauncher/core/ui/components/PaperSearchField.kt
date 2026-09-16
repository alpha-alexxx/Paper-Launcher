package com.epaperlauncher.core.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.epaperlauncher.core.ui.Spacing
import com.epaperlauncher.core.ui.theme.PaperTheme

/**
 * Minimalist search field for E-Paper Launcher
 * - No shadows, no elevation
 * - Subtle background fill
 * - Clean typography
 * - Hairline border
 */
@Composable
fun PaperSearchField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "Search apps...",
    enabled: Boolean = true,
    readOnly: Boolean = false,
) {
    val colors = PaperTheme.colors
    val typography = PaperTheme.typography
    
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = {
            androidx.compose.material3.Text(
                text = placeholder,
                style = typography.bodyLarge,
                color = colors.onSurfaceVariant.copy(alpha = 0.6f)
            )
        },
        textStyle = TextStyle(
            fontFamily = typography.bodyLarge.fontFamily,
            fontSize = typography.bodyLarge.fontSize,
            color = colors.onSurface
        ),
        enabled = enabled,
        readOnly = readOnly,
        singleLine = true,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = colors.surface,
            unfocusedContainerColor = colors.surface,
            disabledContainerColor = colors.surfaceVariant,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            cursorColor = colors.onSurface,
            focusedPlaceholderColor = colors.onSurfaceVariant.copy(alpha = 0.6f),
            unfocusedPlaceholderColor = colors.onSurfaceVariant.copy(alpha = 0.6f),
        ),
    )
}
