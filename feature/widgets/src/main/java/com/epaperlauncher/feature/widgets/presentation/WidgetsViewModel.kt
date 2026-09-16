package com.epaperlauncher.feature.widgets.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.epaperlauncher.feature.widgets.domain.model.CalendarWidgetData
import com.epaperlauncher.feature.widgets.domain.model.TodoWidgetData
import com.epaperlauncher.feature.widgets.domain.model.WeatherWidgetData
import com.epaperlauncher.feature.widgets.domain.repository.WidgetRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI state for the widgets feature
 */
data class WidgetsUiState(
    val weatherData: WeatherWidgetData? = null,
    val calendarData: CalendarWidgetData? = null,
    val todoData: TodoWidgetData? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

/**
 * ViewModel for managing widget data and interactions
 */
@HiltViewModel
class WidgetsViewModel @Inject constructor(
    private val widgetRepository: WidgetRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WidgetsUiState())
    val uiState: StateFlow<WidgetsUiState> = _uiState.asStateFlow()

    init {
        loadWidgetData()
    }

    /**
     * Load all widget data
     */
    private fun loadWidgetData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                // Combine flows from all repositories
                combine(
                    widgetRepository.observeWeatherData("weather_default"),
                    widgetRepository.observeCalendarData("calendar_default"),
                    widgetRepository.observeTodoData("todo_default")
                ) { weather, calendar, todo ->
                    _uiState.update { 
                        it.copy(
                            weatherData = weather,
                            calendarData = calendar,
                            todoData = todo,
                            isLoading = false,
                            error = null
                        )
                    }
                }.launchIn(viewModelScope)
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Unknown error loading widgets"
                    )
                }
            }
        }
    }

    /**
     * Refresh all widget data
     */
    fun refreshAllData() {
        viewModelScope.launch {
            try {
                widgetRepository.refreshWeatherData()
                widgetRepository.refreshCalendarData()
                widgetRepository.refreshTodoData()
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(error = e.message ?: "Failed to refresh data")
                }
            }
        }
    }

    /**
     * Toggle task completion status
     */
    fun toggleTaskCompletion(taskId: String) {
        viewModelScope.launch {
            widgetRepository.toggleTaskCompletion(taskId)
        }
    }

    /**
     * Add a new task
     */
    fun addTask(title: String, dueDate: Long? = null) {
        viewModelScope.launch {
            widgetRepository.addTask(title, dueDate)
        }
    }

    /**
     * Clear any error state
     */
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
