package com.epaperlauncher.feature.widgets.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.epaperlauncher.core.ui.PaperTokens
import com.epaperlauncher.core.ui.Spacing
import com.epaperlauncher.feature.widgets.domain.model.CalendarEvent
import com.epaperlauncher.feature.widgets.domain.model.CalendarWidgetData
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Paper-styled calendar widget showing upcoming events
 */
@Composable
fun CalendarWidget(
    data: CalendarWidgetData,
    modifier: Modifier = Modifier,
    showGrain: Boolean = true,
    cornerRadius: Int = 2,
    maxEvents: Int = 5,
    onEventClick: ((CalendarEvent) -> Unit)? = null
) {
    val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())

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
        contentAlignment = Alignment.TopStart
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            // Header with current date
            Text(
                text = data.currentDate,
                style = MaterialTheme.typography.titleLarge,
                color = PaperTokens.warmInk
            )

            // Divider
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(PaperTokens.warmDivider)
            )

            // Events list
            if (data.events.isEmpty()) {
                Text(
                    text = "No upcoming events",
                    style = MaterialTheme.typography.bodyMedium,
                    color = PaperTokens.warmInkMuted
                )
            } else {
                data.events.take(maxEvents).forEach { event ->
                    EventRow(
                        event = event,
                        timeFormat = timeFormat,
                        onClick = onEventClick?.let { { it(event) } }
                    )
                }

                if (data.events.size > maxEvents) {
                    Text(
                        text = "+${data.events.size - maxEvents} more",
                        style = MaterialTheme.typography.bodySmall,
                        color = PaperTokens.warmInkMuted
                    )
                }
            }
        }
    }
}

@Composable
private fun EventRow(
    event: CalendarEvent,
    timeFormat: SimpleDateFormat,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) {
                    Modifier.clickable(onClick = onClick)
                } else {
                    Modifier
                }
            )
            .padding(vertical = Spacing.xs),
        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
        verticalAlignment = Alignment.Top
    ) {
        // Time column
        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier.width(60.dp)
        ) {
            if (!event.isAllDay) {
                Text(
                    text = timeFormat.format(event.startTime),
                    style = MaterialTheme.typography.labelMedium,
                    color = PaperTokens.warmInk
                )
                Text(
                    text = "-",
                    style = MaterialTheme.typography.labelMedium,
                    color = PaperTokens.warmInkMuted
                )
                Text(
                    text = timeFormat.format(event.endTime),
                    style = MaterialTheme.typography.labelMedium,
                    color = PaperTokens.warmInkMuted
                )
            } else {
                Text(
                    text = "All day",
                    style = MaterialTheme.typography.labelMedium,
                    color = PaperTokens.warmInkMuted
                )
            }
        }

        // Event details column
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(Spacing.xs)
        ) {
            Text(
                text = event.title,
                style = MaterialTheme.typography.bodyLarge,
                color = PaperTokens.warmInk,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            if (event.location != null) {
                Text(
                    text = event.location,
                    style = MaterialTheme.typography.bodySmall,
                    color = PaperTokens.warmInkMuted
                )
            }
        }
    }
}
