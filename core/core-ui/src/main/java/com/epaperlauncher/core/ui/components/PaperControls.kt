package com.epaperlauncher.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.epaperlauncher.core.ui.Spacing
import com.epaperlauncher.core.ui.theme.PaperTheme

/**
 * Choice chip for settings selections
 * - Flat, no elevation
 * - Hairline border when unselected
 * - Filled background when selected
 * - Sharp corners (minimal radius)
 */
@Composable
fun PaperChoiceChip(
    modifier: Modifier = Modifier,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    enabled: Boolean = true,
) {
    val colors = PaperTheme.colors
    val typography = PaperTheme.typography
    
    val interactionSource = remember { MutableInteractionSource() }
    
    Box(
        modifier = modifier
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(2.dp))
            .then(
                if (selected) {
                    Modifier.background(colors.onSurface)
                } else {
                    Modifier
                        .background(colors.surface)
                        .border(
                            width = Spacing.hairline,
                            color = colors.outline,
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(2.dp)
                        )
                }
            )
            .then(
                if (enabled) {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        role = Role.RadioButton,
                        onClick = onClick
                    )
                } else {
                    Modifier
                }
            )
            .padding(
                horizontal = Spacing.md,
                vertical = Spacing.sm
            ),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.material3.Text(
            text = label,
            style = typography.labelMedium,
            color = if (selected) {
                colors.surface
            } else {
                if (enabled) colors.onSurface else colors.onSurfaceVariant
            },
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Toggle switch row for boolean settings
 * - Clean label on left
 * - Minimal toggle indicator on right
 * - Hairline divider below
 */
@Composable
fun PaperToggleRow(
    modifier: Modifier = Modifier,
    label: String,
    supportingText: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true,
) {
    val colors = PaperTheme.colors
    val typography = PaperTheme.typography
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                role = Role.Switch,
                onClick = { onCheckedChange(!checked) }
            )
            .padding(
                horizontal = Spacing.listItemPaddingHorizontal,
                vertical = Spacing.listItemPaddingVertical
            ),
        verticalArrangement = Arrangement.spacedBy(Spacing.xs)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(Spacing.xs)
            ) {
                androidx.compose.material3.Text(
                    text = label,
                    style = typography.bodyLarge,
                    color = if (enabled) colors.onSurface else colors.onSurfaceVariant
                )
                
                if (supportingText != null) {
                    androidx.compose.material3.Text(
                        text = supportingText,
                        style = typography.bodySmall,
                        color = colors.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
            
            // Minimal toggle indicator (square, not rounded)
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(2.dp))
                    .background(
                        if (checked) colors.onSurface else colors.outline
                    )
                    .padding(2.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(if (checked) 16.dp else 0.dp)
                        .clip(androidx.compose.foundation.shape.RoundedCornerShape(1.dp))
                        .background(colors.surface)
                )
            }
        }
        
        // Hairline divider
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(Spacing.hairline)
                .background(colors.outlineVariant)
        )
    }
}
