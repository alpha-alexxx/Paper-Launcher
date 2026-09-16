package com.epaperlauncher.core.data.domain.usecase

import com.epaperlauncher.core.data.domain.model.AppEntry
import com.epaperlauncher.core.data.domain.repository.AppListRepository
import javax.inject.Inject

/**
 * Use case for searching apps by label.
 */
class SearchAppsUseCase @Inject constructor(
    private val appListRepository: AppListRepository
) {
    suspend operator fun invoke(query: String): List<AppEntry> {
        if (query.isBlank()) return emptyList()
        return appListRepository.searchApps(query.trim())
    }
}
