package com.epaperlauncher.feature.launcherdrawer.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.epaperlauncher.core.data.domain.model.AppEntry
import com.epaperlauncher.core.ui.PaperTokens

/**
 * App drawer screen showing all installed apps in a scrollable list.
 */
@Composable
fun DrawerScreen(
    apps: List<AppEntry>,
    isLoading: Boolean,
    onAppClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PaperTokens.warmBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 24.dp)
        ) {
            Text(
                text = "All Apps",
                style = MaterialTheme.typography.titleLarge,
                color = PaperTokens.warmInk,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = PaperTokens.warmInk
                        )
                    }
                }

                apps.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No apps available",
                            style = MaterialTheme.typography.bodyLarge,
                            color = PaperTokens.warmInkMuted
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(
                            items = apps,
                            key = { it.packageName }
                        ) { app ->
                            DrawerAppItem(
                                app = app,
                                onClick = { onAppClick(app.packageName) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DrawerAppItem(
    app: AppEntry,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon placeholder
        Spacer(
            modifier = Modifier
                .size(40.dp)
                .background(PaperTokens.warmDivider, MaterialTheme.shapes.small)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = app.label,
            style = MaterialTheme.typography.bodyLarge,
            color = PaperTokens.warmInk,
            maxLines = 1
        )
    }
}
