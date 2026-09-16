package com.epaperlauncher.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import com.epaperlauncher.core.ui.Spacing
import com.epaperlauncher.core.ui.theme.PaperTheme

/**
 * Minimalist button for E-Paper Launcher
 * - No shadows, no elevation
 * - Flat, stamp-like appearance
 * - Sharp corners (0dp radius)
 * - Clean typography
 */
@Composable
fun PaperButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    isPrimary: Boolean = true,
) {
    val colors = PaperTheme.colors
    val typography = PaperTheme.typography
    
    val interactionSource = remember { MutableInteractionSource() }
    
    Box(
        modifier = modifier
            .background(
                color = if (isPrimary) {
                    if (enabled) colors.onSurface else colors.onSurfaceVariant.copy(alpha = 0.5f)
                } else {
                    colors.surface
                }
            )
            .then(
                if (enabled) {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        role = Role.Button,
                        onClick = onClick
                    )
                } else {
                    Modifier
                }
            )
            .padding(
                horizontal = Spacing.lg,
                vertical = Spacing.sm
            ),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.material3.Text(
            text = text,
            style = typography.labelLarge,
            color = if (isPrimary) {
                if (enabled) colors.surface else colors.onSurfaceVariant
            } else {
                colors.onSurface
            },
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Text-only button variant (no background fill)
 */
@Composable
fun PaperTextButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
) {
    val colors = PaperTheme.colors
    val typography = PaperTheme.typography
    
    val interactionSource = remember { MutableInteractionSource() }
    
    androidx.compose.material3.Text(
        text = text,
        modifier = modifier
            .then(
                if (enabled) {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        role = Role.Button,
                        onClick = onClick
                    )
                } else {
                    Modifier
                }
            )
            .padding(
                horizontal = Spacing.sm,
                vertical = Spacing.xs
            ),
        style = typography.labelMedium,
        color = if (enabled) colors.onSurface else colors.onSurfaceVariant,
        textAlign = TextAlign.Center
    )
}
