package com.epaperlauncher.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.epaperlauncher.core.ui.Spacing
import com.epaperlauncher.core.ui.theme.PaperTheme

/**
 * Paper grain texture overlay
 * Subtle noise texture to simulate real paper
 * Applied as a semi-transparent layer over backgrounds
 */
@Composable
fun PaperGrainOverlay(
    modifier: Modifier = Modifier,
    intensity: Float = 0.08f, // 0.0 - 0.3
) {
    val colors = PaperTheme.colors
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .drawBehind {
                // Simple procedural grain using random noise
                // In production, this would use a tileable PNG texture
                val grainSize = 2.dp.toPx()
                val stepsX = size.width / grainSize
                val stepsY = size.height / grainSize
                
                for (x in 0 until stepsX.toInt()) {
                    for (y in 0 until stepsY.toInt()) {
                        val alpha = (0.3f + kotlin.random.Random.nextFloat() * 0.4f) * intensity
                        drawCircle(
                            color = colors.onSurface.copy(alpha = alpha),
                            radius = grainSize / 2,
                            center = Offset(
                                x * grainSize + grainSize / 2,
                                y * grainSize + grainSize / 2
                            )
                        )
                    }
                }
            }
    )
}

/**
 * Hairline divider component
 * Can be used standalone or as part of other components
 */
@Composable
fun PaperDivider(
    modifier: Modifier = Modifier,
    thickness: Float = Spacing.hairline.value,
    color: androidx.compose.ui.graphics.Color? = null,
) {
    val colors = PaperTheme.colors
    
    Spacer(
        modifier = modifier
            .fillMaxWidth()
            .height(thickness.dp)
            .background(color ?: colors.outlineVariant)
    )
}

/**
 * Vertical hairline divider
 */
@Composable
fun PaperVerticalDivider(
    modifier: Modifier = Modifier,
    thickness: Float = Spacing.hairline.value,
    color: androidx.compose.ui.graphics.Color? = null,
) {
    val colors = PaperTheme.colors
    
    Spacer(
        modifier = modifier
            .fillMaxHeight()
            .width(thickness.dp)
            .background(color ?: colors.outlineVariant)
    )
}
