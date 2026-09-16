package com.epaperlauncher.feature.widgets

import com.epaperlauncher.feature.widgets.data.repository.WidgetRepositoryImpl
import com.epaperlauncher.feature.widgets.domain.model.TaskPriority
import com.epaperlauncher.feature.widgets.domain.model.WeatherCondition
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WidgetRepositoryImplTest {

    private lateinit var repository: WidgetRepositoryImpl

    @Before
    fun setup() {
        repository = WidgetRepositoryImpl()
    }

    @Test
    fun `repository initializes with sample data`() = runTest {
        val weatherData = repository.getWeatherData("weather_default")
        val calendarData = repository.getCalendarData("calendar_default")
        val todoData = repository.getTodoData("todo_default")

        assertNotNull(weatherData)
        assertNotNull(calendarData)
        assertNotNull(todoData)

        assertEquals("San Francisco", weatherData?.location)
        assertTrue(weatherData?.temperature ?: 0f > 0f)
        
        assertTrue(calendarData?.events?.isNotEmpty() ?: false)
        
        assertTrue(todoData?.tasks?.isNotEmpty() ?: false)
        assertEquals(todoData?.completedCount ?: 0 + todoData?.totalCount ?: 0, todoData?.totalCount)
    }

    @Test
    fun `toggleTaskCompletion updates task state`() = runTest {
        val initialData = repository.getTodoData("todo_default")
        assertNotNull(initialData)
        
        val incompleteTask = initialData!!.tasks.first { !it.isCompleted }
        
        repository.toggleTaskCompletion(incompleteTask.id)
        
        val updatedData = repository.getTodoData("todo_default")
        val updatedTask = updatedData?.tasks?.find { it.id == incompleteTask.id }
        
        assertTrue(updatedTask?.isCompleted ?: false)
        assertEquals(initialData.completedCount + 1, updatedData?.completedCount)
    }

    @Test
    fun `addTask increases total count`() = runTest {
        val initialData = repository.getTodoData("todo_default")
        assertNotNull(initialData)
        
        val initialCount = initialData!!.totalCount
        
        repository.addTask("New test task")
        
        val updatedData = repository.getTodoData("todo_default")
        assertEquals(initialCount + 1, updatedData?.totalCount)
        
        val newTask = updatedData?.tasks?.find { it.title == "New test task" }
        assertNotNull(newTask)
        assertFalse(newTask?.isCompleted ?: true)
        assertEquals(TaskPriority.NORMAL, newTask?.priority)
    }

    @Test
    fun `refreshWeatherData updates timestamp and temperature`() = runTest {
        val initialData = repository.getWeatherData("weather_default")
        assertNotNull(initialData)
        
        val initialTemp = initialData!!.temperature
        val initialTimestamp = initialData.lastUpdated
        
        Thread.sleep(10) // Ensure time difference
        
        repository.refreshWeatherData()
        
        val updatedData = repository.getWeatherData("weather_default")
        assertTrue(updatedData!!.lastUpdated > initialTimestamp)
        // Temperature may change slightly due to randomization
        assertTrue(kotlin.math.abs(updatedData.temperature - initialTemp) <= 2f)
    }

    @Test
    fun `observeWeatherData emits initial value`() = runTest {
        val flow = repository.observeWeatherData("weather_default")
        val result = flow.first()
        
        assertNotNull(result)
        assertEquals("weather_default", result?.widgetId)
    }

    @Test
    fun `observeCalendarData emits events`() = runTest {
        val flow = repository.observeCalendarData("calendar_default")
        val result = flow.first()
        
        assertNotNull(result)
        assertTrue(result?.events?.isNotEmpty() ?: false)
        assertNotNull(result?.currentDate)
    }

    @Test
    fun `observeTodoData emits tasks with correct counts`() = runTest {
        val flow = repository.observeTodoData("todo_default")
        val result = flow.first()
        
        assertNotNull(result)
        assertEquals(result?.completedCount, result?.tasks?.count { it.isCompleted })
        assertEquals(result?.totalCount, result?.tasks?.size)
    }

    @Test
    fun `weather data has valid condition`() = runTest {
        val weatherData = repository.getWeatherData("weather_default")
        
        assertNotNull(weatherData)
        assertTrue(WeatherCondition.values().contains(weatherData?.condition))
        assertNotNull(weatherData?.condition?.displayName)
    }

    @Test
    fun `todo tasks have valid priorities`() = runTest {
        val todoData = repository.getTodoData("todo_default")
        
        assertNotNull(todoData)
        todoData?.tasks?.forEach { task ->
            assertTrue(TaskPriority.values().contains(task.priority))
        }
    }
}
