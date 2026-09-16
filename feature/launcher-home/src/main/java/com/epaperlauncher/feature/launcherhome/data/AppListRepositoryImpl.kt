package com.epaperlauncher.feature.launcherhome.data

import com.epaperlauncher.core.data.domain.model.AppEntry
import com.epaperlauncher.core.data.domain.repository.AppListRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Implementation of AppListRepository for the launcher-home feature.
 * This is a thin wrapper that delegates to the core-data implementation.
 */
class AppListRepositoryImpl @Inject constructor(
    private val appListRepository: AppListRepository
) : AppListRepository by appListRepository
