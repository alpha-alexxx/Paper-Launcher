package com.epaperlauncher.feature.launcherhome.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.painter.Painter
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.epaperlauncher.core.data.domain.model.AppEntry
import com.epaperlauncher.core.ui.Spacing
import com.epaperlauncher.core.ui.components.PaperDivider
import com.epaperlauncher.core.ui.theme.PaperTheme

/**
 * Minimalist app list item for E-Paper Launcher
 * - No shadows, no elevation
 * - Hairline divider at bottom
 * - Clean typography-led layout
 * - Swipe gestures for quick actions
 */
@Composable
fun AppListItem(
    app: AppEntry,
    iconPainter: Painter?,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onSwipeLeft: () -> Unit = {},
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
) {
    val colors = PaperTheme.colors
    val typography = PaperTheme.typography
    
    val interactionSource = remember { MutableInteractionSource() }
    var isSwiping = false
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = if (isSelected) colors.selected else colors.background
            )
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        if (isSwiping) {
                            onSwipeLeft()
                            isSwiping = false
                        }
                    },
                    onHorizontalDrag = { change, dragAmount ->
                        if (dragAmount < -10f) {
                            isSwiping = true
                        }
                    }
                )
            }
            .then(
                if (!isSwiping) {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        role = Role.Button,
                        onClick = onClick,
                        onLongClick = onLongClick
                    )
                } else {
                    Modifier
                }
            )
            .padding(
                horizontal = Spacing.listItemPaddingHorizontal,
                vertical = Spacing.listItemPaddingVertical
            )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            // Icon (if available)
            if (iconPainter != null) {
                androidx.compose.foundation.Image(
                    painter = iconPainter,
                    contentDescription = "${app.label} icon",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(androidx.compose.foundation.shape.RoundedCornerShape(2.dp))
                )
            } else {
                // Placeholder for missing icon
                Spacer(
                    modifier = Modifier
                        .size(40.dp)
                        .background(colors.outline, androidx.compose.foundation.shape.RoundedCornerShape(2.dp))
                )
            }

            // App label
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                androidx.compose.material3.Text(
                    text = app.label,
                    style = typography.bodyLarge,
                    color = colors.onBackground,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                if (app.isSystemApp) {
                    androidx.compose.material3.Text(
                        text = "System",
                        style = typography.bodySmall,
                        color = colors.onSurfaceVariant.copy(alpha = 0.7f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
        
        // Hairline divider at bottom
        PaperDivider(
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
