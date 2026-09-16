package com.epaperlauncher.feature.launcherhome.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.epaperlauncher.core.ui.Spacing
import com.epaperlauncher.core.ui.components.PaperSectionHeader
import com.epaperlauncher.core.ui.theme.PaperTheme
import com.epaperlauncher.feature.launcherhome.presentation.components.AppListItem
import com.epaperlauncher.feature.launcherhome.presentation.components.SearchBar

/**
 * Home screen composable displaying the Niagara-style vertical app list
 * - Typography-led layout
 * - Warm cream paper background
 * - Flat grayscale palette
 * - Hairline dividers
 * - No shadows, no gradients
 */
@Composable
fun HomeScreen(
    onAppClick: (String) -> Unit,
    onAppLongClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val colors = PaperTheme.colors
    val typography = PaperTheme.typography
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = Spacing.screenMarginHorizontal, vertical = Spacing.lg)
        ) {
            // Header with date/time could go here
            androidx.compose.material3.Text(
                text = "Apps",
                style = typography.titleLarge,
                color = colors.onBackground,
                modifier = Modifier.padding(bottom = Spacing.md)
            )
            
            // Search bar
            SearchBar(
                query = uiState.searchQuery,
                onQueryChange = { viewModel.onSearchQueryChanged(it) },
                onClose = { viewModel.onSearchQueryChanged("") },
                modifier = Modifier.padding(bottom = Spacing.md)
            )

            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        androidx.compose.material3.CircularProgressIndicator(
                            color = colors.onSurface,
                            strokeWidth = 2.dp
                        )
                    }
                }

                uiState.apps.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        androidx.compose.material3.Text(
                            text = if (uiState.searchQuery.isNotEmpty()) {
                                "No apps match \"$uiState.searchQuery\""
                            } else {
                                "No apps found"
                            },
                            style = typography.bodyLarge,
                            color = colors.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(Spacing.none)
                    ) {
                        items(
                            items = uiState.apps,
                            key = { it.packageName }
                        ) { app ->
                            AppListItem(
                                app = app,
                                iconPainter = null, // TODO: Load icon from path via IconRepository
                                onClick = { onAppClick(app.packageName) },
                                onLongClick = { onAppLongClick(app.packageName) },
                                onSwipeLeft = { /* TODO: Show quick actions */ }
                            )
                        }
                    }
                }
            }
        }
    }
}
