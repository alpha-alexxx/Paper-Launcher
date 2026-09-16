package com.epaperlauncher.feature.launcherhome.domain.usecase

import com.epaperlauncher.core.data.domain.model.AppEntry
import com.epaperlauncher.core.data.domain.repository.AppListRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Use case for getting sorted apps based on user preferences.
 * Sorting order: pinned apps first, then by most-used, then alphabetically.
 */
class GetSortedAppsUseCase @Inject constructor(
    private val appListRepository: AppListRepository
) {
    operator fun invoke(): Flow<List<AppEntry>> {
        return appListRepository.observeApps()
            .map { apps ->
                apps
                    .filter { !it.isHidden }
                    .sortedWith(compareByDescending<AppEntry> { it.isPinned }
                        .thenByDescending { it.launchCount }
                        .thenBy { it.label.lowercase() })
            }
    }
}
