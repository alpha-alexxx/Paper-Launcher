package com.epaperlauncher.feature.widgets.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.epaperlauncher.core.ui.PaperTokens
import com.epaperlauncher.core.ui.Spacing
import com.epaperlauncher.feature.widgets.domain.model.WeatherCondition
import com.epaperlauncher.feature.widgets.domain.model.WeatherWidgetData

/**
 * Paper-styled weather widget showing current conditions
 */
@Composable
fun WeatherWidget(
    data: WeatherWidgetData,
    modifier: Modifier = Modifier,
    showGrain: Boolean = true,
    cornerRadius: Int = 2
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius.dp))
            .background(PaperTokens.warmSurface)
            .border(
                width = 1.dp,
                color = PaperTokens.warmDivider,
                shape = RoundedCornerShape(cornerRadius.dp)
            )
            .padding(Spacing.lg),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            // Location
            Text(
                text = data.location,
                style = MaterialTheme.typography.labelMedium,
                color = PaperTokens.warmInkMuted
            )

            // Temperature and condition
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                Text(
                    text = "${data.temperature.toInt()}°",
                    style = MaterialTheme.typography.displayMedium,
                    color = PaperTokens.warmInk
                )
                
                Text(
                    text = data.condition.displayName,
                    style = MaterialTheme.typography.bodyLarge,
                    color = PaperTokens.warmInkMuted
                )
            }

            // High/Low temps
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                Text(
                    text = "H:${data.highTemp.toInt()}°",
                    style = MaterialTheme.typography.bodyMedium,
                    color = PaperTokens.warmInkMuted
                )
                Text(
                    text = "L:${data.lowTemp.toInt()}°",
                    style = MaterialTheme.typography.bodyMedium,
                    color = PaperTokens.warmInkMuted
                )
            }

            // Humidity and wind
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.lg)
            ) {
                Text(
                    text = "💧 ${data.humidity}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = PaperTokens.warmInkMuted
                )
                Text(
                    text = "💨 ${data.windSpeed} km/h",
                    style = MaterialTheme.typography.bodySmall,
                    color = PaperTokens.warmInkMuted
                )
            }

            // Last updated
            Text(
                text = "Updated ${formatLastUpdated(data.lastUpdated)}",
                style = MaterialTheme.typography.caption,
                color = PaperTokens.warmInkMuted.copy(alpha = 0.7f)
            )
        }
    }
}

private fun formatLastUpdated(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    return when {
        diff < 60000 -> "just now"
        diff < 3600000 -> "${diff / 60000}m ago"
        diff < 86400000 -> "${diff / 3600000}h ago"
        else -> "${diff / 86400000}d ago"
    }
}

@Preview(showBackground = true)
@Composable
private fun WeatherWidgetPreview() {
    val sampleData = WeatherWidgetData(
        widgetId = "preview",
        lastUpdated = System.currentTimeMillis(),
        temperature = 22.5f,
        condition = WeatherCondition.PARTLY_CLOUDY,
        location = "San Francisco",
        highTemp = 26.0f,
        lowTemp = 15.0f,
        humidity = 65,
        windSpeed = 12.5f
    )
    
    WeatherWidget(
        data = sampleData,
        modifier = Modifier
            .width(280.dp)
            .height(200.dp)
    )
}
