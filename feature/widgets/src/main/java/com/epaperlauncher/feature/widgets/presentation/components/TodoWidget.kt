package com.epaperlauncher.feature.widgets.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.epaperlauncher.core.ui.PaperTokens
import com.epaperlauncher.core.ui.Spacing
import com.epaperlauncher.feature.widgets.domain.model.TodoTask
import com.epaperlauncher.feature.widgets.domain.model.TodoWidgetData

/**
 * Paper-styled todo widget showing tasks with completion checkboxes
 */
@Composable
fun TodoWidget(
    data: TodoWidgetData,
    modifier: Modifier = Modifier,
    showGrain: Boolean = true,
    cornerRadius: Int = 2,
    maxTasks: Int = 5,
    onTaskToggle: (String) -> Unit = {},
    onAddTask: () -> Unit = {}
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
        contentAlignment = Alignment.TopStart
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            // Header with progress
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "My Tasks",
                    style = MaterialTheme.typography.titleLarge,
                    color = PaperTokens.warmInk
                )
                
                // Progress indicator
                Text(
                    text = "${data.completedCount}/${data.totalCount}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = PaperTokens.warmInkMuted
                )
            }

            // Progress bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(PaperTokens.warmDivider)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(data.completedCount.toFloat() / data.totalCount.coerceAtLeast(1))
                        .height(4.dp)
                        .background(PaperTokens.warmInk)
                )
            }

            // Tasks list
            if (data.tasks.isEmpty()) {
                Text(
                    text = "No tasks yet",
                    style = MaterialTheme.typography.bodyMedium,
                    color = PaperTokens.warmInkMuted
                )
            } else {
                data.tasks.take(maxTasks).forEach { task ->
                    TaskRow(
                        task = task,
                        onToggle = { onTaskToggle(task.id) }
                    )
                }

                if (data.tasks.size > maxTasks) {
                    Text(
                        text = "+${data.tasks.size - maxTasks} more",
                        style = MaterialTheme.typography.bodySmall,
                        color = PaperTokens.warmInkMuted
                    )
                }
            }

            // Add task button
            Text(
                text = "+ Add task",
                style = MaterialTheme.typography.bodyMedium,
                color = PaperTokens.warmInkMuted,
                modifier = Modifier
                    .clickable(onClick = onAddTask)
                    .padding(vertical = Spacing.xs)
            )
        }
    }
}

@Composable
private fun TaskRow(
    task: TodoTask,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
            .padding(vertical = Spacing.xs),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Checkbox
        Checkbox(
            checked = task.isCompleted,
            onCheckedChange = { onToggle() },
            colors = androidx.compose.material3.CheckboxDefaults.colors(
                checkedColor = PaperTokens.warmInk,
                uncheckedColor = PaperTokens.warmInkMuted
            )
        )

        // Task title
        Text(
            text = task.title,
            style = MaterialTheme.typography.bodyLarge,
            color = if (task.isCompleted) PaperTokens.warmInkMuted else PaperTokens.warmInk,
            textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null,
            modifier = Modifier.weight(1f),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        // Priority indicator
        when (task.priority) {
            com.epaperlauncher.feature.widgets.domain.model.TaskPriority.HIGH -> {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(PaperTokens.warmInk)
                )
            }
            com.epaperlauncher.feature.widgets.domain.model.TaskPriority.NORMAL -> {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(PaperTokens.warmInkMuted.copy(alpha = 0.5f))
                )
            }
            com.epaperlauncher.feature.widgets.domain.model.TaskPriority.LOW -> {
                // No indicator for low priority
            }
        }
    }
}
