package com.epaperlauncher.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.epaperlauncher.core.ui.Spacing
import com.epaperlauncher.core.ui.theme.PaperTheme

/**
 * Section header for E-Paper Launcher
 * - Clean typography hierarchy
 * - Hairline divider below
 * - No shadows, no decoration
 */
@Composable
fun PaperSectionHeader(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String? = null,
) {
    val colors = PaperTheme.colors
    val typography = PaperTheme.typography
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = Spacing.listItemPaddingHorizontal,
                vertical = Spacing.sm
            ),
        verticalArrangement = Arrangement.spacedBy(Spacing.xs)
    ) {
        Text(
            text = title,
            style = typography.titleSmall,
            color = colors.onSurfaceVariant,
            textAlign = TextAlign.Start
        )
        
        if (subtitle != null) {
            Text(
                text = subtitle,
                style = typography.bodySmall,
                color = colors.onSurfaceVariant.copy(alpha = 0.7f),
                textAlign = TextAlign.Start
            )
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
