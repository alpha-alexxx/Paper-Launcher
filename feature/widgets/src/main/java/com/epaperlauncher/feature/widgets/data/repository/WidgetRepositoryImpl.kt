package com.epaperlauncher.feature.widgets.data.repository

import com.epaperlauncher.feature.widgets.domain.model.CalendarEvent
import com.epaperlauncher.feature.widgets.domain.model.CalendarWidgetData
import com.epaperlauncher.feature.widgets.domain.model.TaskPriority
import com.epaperlauncher.feature.widgets.domain.model.TodoTask
import com.epaperlauncher.feature.widgets.domain.model.TodoWidgetData
import com.epaperlauncher.feature.widgets.domain.model.WeatherCondition
import com.epaperlauncher.feature.widgets.domain.model.WeatherWidgetData
import com.epaperlauncher.feature.widgets.domain.repository.WidgetRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of WidgetRepository with mock/sample data
 * In production, this would connect to real APIs and ContentProviders
 */
@Singleton
class WidgetRepositoryImpl @Inject constructor() : WidgetRepository {

    private val weatherDataStore = MutableStateFlow<Map<String, WeatherWidgetData>>(emptyMap())
    private val calendarDataStore = MutableStateFlow<Map<String, CalendarWidgetData>>(emptyMap())
    private val todoDataStore = MutableStateFlow<Map<String, TodoWidgetData>>(emptyMap())

    private val dateFormat = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault())

    init {
        // Initialize with sample data for demonstration
        initializeSampleData()
    }

    private fun initializeSampleData() {
        // Sample weather data
        val sampleWeather = WeatherWidgetData(
            widgetId = "weather_default",
            lastUpdated = System.currentTimeMillis(),
            temperature = 22.5f,
            condition = WeatherCondition.PARTLY_CLOUDY,
            location = "San Francisco",
            highTemp = 26.0f,
            lowTemp = 15.0f,
            humidity = 65,
            windSpeed = 12.5f
        )
        weatherDataStore.update { it + (sampleWeather.widgetId to sampleWeather) }

        // Sample calendar data
        val now = Calendar.getInstance()
        val events = listOf(
            CalendarEvent(
                title = "Team Standup",
                startTime = now.apply { set(Calendar.HOUR_OF_DAY, 9); set(Calendar.MINUTE, 0) }.timeInMillis,
                endTime = now.apply { set(Calendar.HOUR_OF_DAY, 9); set(Calendar.MINUTE, 30) }.timeInMillis,
                location = "Conference Room A",
                isAllDay = false
            ),
            CalendarEvent(
                title = "Product Review",
                startTime = now.apply { set(Calendar.HOUR_OF_DAY, 14); set(Calendar.MINUTE, 0) }.timeInMillis,
                endTime = now.apply { set(Calendar.HOUR_OF_DAY, 15); set(Calendar.MINUTE, 0) }.timeInMillis,
                location = null,
                isAllDay = false
            ),
            CalendarEvent(
                title = "Design Workshop",
                startTime = now.apply { add(Calendar.DAY_OF_MONTH, 1); set(Calendar.HOUR_OF_DAY, 10); set(Calendar.MINUTE, 0) }.timeInMillis,
                endTime = now.apply { set(Calendar.HOUR_OF_DAY, 12); set(Calendar.MINUTE, 0) }.timeInMillis,
                location = "Design Lab",
                isAllDay = false
            )
        )
        val sampleCalendar = CalendarWidgetData(
            widgetId = "calendar_default",
            lastUpdated = System.currentTimeMillis(),
            events = events,
            currentDate = dateFormat.format(System.currentTimeMillis())
        )
        calendarDataStore.update { it + (sampleCalendar.widgetId to sampleCalendar) }

        // Sample todo data
        val tasks = listOf(
            TodoTask(
                id = "1",
                title = "Review pull requests",
                isCompleted = false,
                priority = TaskPriority.HIGH,
                dueDate = System.currentTimeMillis() + 86400000
            ),
            TodoTask(
                id = "2",
                title = "Update documentation",
                isCompleted = true,
                priority = TaskPriority.NORMAL,
                dueDate = null
            ),
            TodoTask(
                id = "3",
                title = "Plan sprint backlog",
                isCompleted = false,
                priority = TaskPriority.NORMAL,
                dueDate = System.currentTimeMillis() + 172800000
            ),
            TodoTask(
                id = "4",
                title = "Fix bug #1234",
                isCompleted = false,
                priority = TaskPriority.HIGH,
                dueDate = System.currentTimeMillis()
            )
        )
        val sampleTodo = TodoWidgetData(
            widgetId = "todo_default",
            lastUpdated = System.currentTimeMillis(),
            tasks = tasks,
            completedCount = tasks.count { it.isCompleted },
            totalCount = tasks.size
        )
        todoDataStore.update { it + (sampleTodo.widgetId to sampleTodo) }
    }

    override fun observeWeatherData(widgetId: String): Flow<WeatherWidgetData?> {
        return MutableStateFlow(weatherDataStore.value[widgetId]).asStateFlow()
    }

    override fun observeCalendarData(widgetId: String): Flow<CalendarWidgetData?> {
        return MutableStateFlow(calendarDataStore.value[widgetId]).asStateFlow()
    }

    override fun observeTodoData(widgetId: String): Flow<TodoWidgetData?> {
        return MutableStateFlow(todoDataStore.value[widgetId]).asStateFlow()
    }

    override suspend fun refreshWeatherData() {
        // In production: fetch from weather API
        // For now, just update the timestamp
        val current = weatherDataStore.value.values.firstOrNull() ?: return
        val updated = current.copy(
            lastUpdated = System.currentTimeMillis(),
            temperature = (current.temperature + (Math.random() * 4 - 2)).toFloat()
        )
        weatherDataStore.update { it + (updated.widgetId to updated) }
    }

    override suspend fun refreshCalendarData() {
        // In production: query CalendarContract ContentProvider
        // For now, just update the timestamp and date
        val current = calendarDataStore.value.values.firstOrNull() ?: return
        val updated = current.copy(
            lastUpdated = System.currentTimeMillis(),
            currentDate = dateFormat.format(System.currentTimeMillis())
        )
        calendarDataStore.update { it + (updated.widgetId to updated) }
    }

    override suspend fun refreshTodoData() {
        // In production: fetch from todo app API or local database
        // For now, just update the timestamp
        val current = todoDataStore.value.values.firstOrNull() ?: return
        val updated = current.copy(
            lastUpdated = System.currentTimeMillis(),
            completedCount = current.tasks.count { it.isCompleted }
        )
        todoDataStore.update { it + (updated.widgetId to updated) }
    }

    override suspend fun toggleTaskCompletion(taskId: String) {
        val currentData = todoDataStore.value.values.firstOrNull() ?: return
        val updatedTasks = currentData.tasks.map { task ->
            if (task.id == taskId) {
                task.copy(isCompleted = !task.isCompleted)
            } else {
                task
            }
        }
        val updated = currentData.copy(
            tasks = updatedTasks,
            lastUpdated = System.currentTimeMillis(),
            completedCount = updatedTasks.count { it.isCompleted }
        )
        todoDataStore.update { it + (updated.widgetId to updated) }
    }

    override suspend fun addTask(title: String, dueDate: Long?) {
        val currentData = todoDataStore.value.values.firstOrNull() ?: return
        val newTask = TodoTask(
            id = System.currentTimeMillis().toString(),
            title = title,
            isCompleted = false,
            priority = TaskPriority.NORMAL,
            dueDate = dueDate
        )
        val updatedTasks = currentData.tasks + newTask
        val updated = currentData.copy(
            tasks = updatedTasks,
            lastUpdated = System.currentTimeMillis(),
            totalCount = updatedTasks.size
        )
        todoDataStore.update { it + (updated.widgetId to updated) }
    }

    // Helper method for widgets to get current data
    fun getWeatherData(widgetId: String): WeatherWidgetData? = weatherDataStore.value[widgetId]
    fun getCalendarData(widgetId: String): CalendarWidgetData? = calendarDataStore.value[widgetId]
    fun getTodoData(widgetId: String): TodoWidgetData? = todoDataStore.value[widgetId]
}
