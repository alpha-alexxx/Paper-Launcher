package com.epaperlauncher.feature.launcherhome.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.epaperlauncher.core.data.domain.model.AppEntry
import com.epaperlauncher.feature.launcherhome.domain.usecase.GetSortedAppsUseCase
import com.epaperlauncher.feature.launcherhome.domain.usecase.SearchAppsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI state for the home screen.
 */
data class HomeUiState(
    val apps: List<AppEntry> = emptyList(),
    val searchQuery: String = "",
    val isSearching: Boolean = false,
    val isLoading: Boolean = true,
    val error: String? = null
)

/**
 * ViewModel for the home screen.
 * Collects app list and handles search functionality.
 */
@OptIn(FlowPreview::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getSortedAppsUseCase: GetSortedAppsUseCase,
    private val searchAppsUseCase: SearchAppsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    init {
        loadApps()
    }

    private fun loadApps() {
        viewModelScope.launch {
            getSortedAppsUseCase()
                .onEach { apps ->
                    _uiState.update { currentState ->
                        currentState.copy(
                            apps = apps,
                            isLoading = false
                        )
                    }
                }
                .launchIn(this)
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query, isLoading = true) }

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            if (query.isBlank()) {
                _uiState.update { it.copy(isSearching = false, isLoading = false) }
            } else {
                // Debounce search to avoid too many queries
                delay(300)
                val results = searchAppsUseCase(query)
                _uiState.update {
                    it.copy(
                        apps = results,
                        isSearching = true,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun clearSearch() {
        onSearchQueryChange("")
    }

    fun recordAppLaunch(packageName: String) {
        viewModelScope.launch {
            // This would call repository.recordLaunch(packageName)
            // For now, just a placeholder
        }
    }
}
