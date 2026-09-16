package com.epaperlauncher.feature.widgets.domain.repository

import com.epaperlauncher.feature.widgets.domain.model.CalendarWidgetData
import com.epaperlauncher.feature.widgets.domain.model.TodoWidgetData
import com.epaperlauncher.feature.widgets.domain.model.WeatherWidgetData
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for widget data sources
 */
interface WidgetRepository {
    /**
     * Observe weather widget data as a Flow
     */
    fun observeWeatherData(widgetId: String): Flow<WeatherWidgetData?>

    /**
     * Observe calendar widget data as a Flow
     */
    fun observeCalendarData(widgetId: String): Flow<CalendarWidgetData?>

    /**
     * Observe todo widget data as a Flow
     */
    fun observeTodoData(widgetId: String): Flow<TodoWidgetData?>

    /**
     * Refresh weather data from external source
     */
    suspend fun refreshWeatherData()

    /**
     * Refresh calendar data from system calendar provider
     */
    suspend fun refreshCalendarData()

    /**
     * Refresh todo data from external source or local storage
     */
    suspend fun refreshTodoData()

    /**
     * Mark a todo task as completed
     */
    suspend fun toggleTaskCompletion(taskId: String)

    /**
     * Add a new todo task
     */
    suspend fun addTask(title: String, dueDate: Long? = null)
}
