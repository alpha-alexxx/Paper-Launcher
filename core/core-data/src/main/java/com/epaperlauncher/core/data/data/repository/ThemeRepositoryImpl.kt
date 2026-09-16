package com.epaperlauncher.core.data.data.repository

import androidx.datastore.core.DataStore
import com.epaperlauncher.core.data.domain.model.ThemeConfig
import com.epaperlauncher.core.data.domain.repository.ThemeRepository
import kotlinx.coroutines.flow.StateFlow

/**
 * Implementation of ThemeRepository using DataStore.
 */
class ThemeRepositoryImpl(
    private val dataStore: DataStore<ThemeConfig>
) : ThemeRepository {

    override fun observeThemeConfig(): StateFlow<ThemeConfig> {
        return dataStore.data
    }

    override suspend fun update(transform: (ThemeConfig) -> ThemeConfig) {
        dataStore.updateData(transform)
    }
}
