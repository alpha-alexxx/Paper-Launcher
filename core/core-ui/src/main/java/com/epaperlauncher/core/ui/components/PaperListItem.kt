package com.epaperlauncher.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.epaperlauncher.core.ui.Spacing
import com.epaperlauncher.core.ui.theme.PaperTheme

/**
 * Minimalist list item for E-Paper Launcher
 * - No shadows, no elevation
 * - Hairline divider at bottom
 * - Clean typography-led layout
 * - Subtle pressed state
 */
@Composable
fun PaperListItem(
    modifier: Modifier = Modifier,
    headline: String,
    supportingText: String? = null,
    trailingContent: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    isSelected: Boolean = false,
) {
    val colors = PaperTheme.colors
    val typography = PaperTheme.typography
    
    val interactionSource = remember { MutableInteractionSource() }
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = if (isSelected) colors.selected else colors.background
            )
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = null, // No ripple, minimal aesthetic
                        role = Role.Button,
                        onClick = onClick
                    )
                } else {
                    Modifier
                }
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
                Text(
                    text = headline,
                    style = typography.bodyLarge,
                    color = colors.onBackground
                )
                
                if (supportingText != null) {
                    Text(
                        text = supportingText,
                        style = typography.bodySmall,
                        color = colors.onSurfaceVariant
                    )
                }
            }
            
            if (trailingContent != null) {
                Box(
                    modifier = Modifier.padding(start = Spacing.sm),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    trailingContent()
                }
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
