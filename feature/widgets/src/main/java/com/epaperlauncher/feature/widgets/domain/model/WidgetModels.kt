package com.epaperlauncher.feature.widgets.domain.model

import kotlinx.serialization.Serializable

/**
 * Base interface for all widget data models
 */
sealed interface WidgetData {
    val widgetId: String
    val lastUpdated: Long
}

/**
 * Weather widget data model
 */
data class WeatherWidgetData(
    override val widgetId: String,
    override val lastUpdated: Long,
    val temperature: Float,
    val condition: WeatherCondition,
    val location: String,
    val highTemp: Float,
    val lowTemp: Float,
    val humidity: Int,
    val windSpeed: Float
) : WidgetData

/**
 * Weather conditions for paper-style display
 */
enum class WeatherCondition(val displayName: String, val iconResId: Int) {
    SUNNY("Sunny", android.R.drawable.ic_menu_today),
    CLOUDY("Cloudy", android.R.drawable.ic_menu_compass),
    RAINY("Rainy", android.R.drawable.ic_menu_mapmode),
    STORMY("Stormy", android.R.drawable.ic_dialog_alert),
    SNOWY("Snowy", android.R.drawable.ic_menu_agenda),
    FOGGY("Foggy", android.R.drawable.ic_menu_search),
    PARTLY_CLOUDY("Partly Cloudy", android.R.drawable.ic_menu_today),
    UNKNOWN("Unknown", android.R.drawable.ic_menu_help)
}

/**
 * Calendar widget data model - shows upcoming events
 */
data class CalendarWidgetData(
    override val widgetId: String,
    override val lastUpdated: Long,
    val events: List<CalendarEvent>,
    val currentDate: String
) : WidgetData

/**
 * Single calendar event
 */
data class CalendarEvent(
    val title: String,
    val startTime: Long,
    val endTime: Long,
    val location: String? = null,
    val isAllDay: Boolean = false,
    val color: Int = 0 // Grayscale value for paper theme
)

/**
 * ToDo widget data model
 */
data class TodoWidgetData(
    override val widgetId: String,
    override val lastUpdated: Long,
    val tasks: List<TodoTask>,
    val completedCount: Int,
    val totalCount: Int
) : WidgetData

/**
 * Single todo task
 */
data class TodoTask(
    val id: String,
    val title: String,
    val isCompleted: Boolean,
    val priority: TaskPriority = TaskPriority.NORMAL,
    val dueDate: Long? = null
)

/**
 * Task priority levels
 */
enum class TaskPriority {
    LOW,
    NORMAL,
    HIGH
}

/**
 * Widget configuration options
 */
@Serializable
data class WidgetConfig(
    val showGrain: Boolean = true,
    val cornerRadius: Int = 2,
    val showDividers: Boolean = true,
    val maxItems: Int = 5
)
