package com.epaperlauncher.core.data.domain.usecase

import com.epaperlauncher.core.data.domain.model.AppEntry
import com.epaperlauncher.core.data.domain.repository.AppListRepository
import javax.inject.Inject

/**
 * Use case for getting sorted app list.
 * Sorting order: pinned → most-used → alphabetical (configurable).
 */
class GetSortedAppsUseCase @Inject constructor(
    private val appListRepository: AppListRepository
) {
    operator fun invoke() = appListRepository.observeApps()
}
