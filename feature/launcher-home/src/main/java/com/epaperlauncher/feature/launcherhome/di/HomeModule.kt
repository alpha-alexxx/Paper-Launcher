package com.epaperlauncher.feature.launcherhome.di

import com.epaperlauncher.core.data.domain.repository.AppListRepository
import com.epaperlauncher.feature.launcherhome.domain.usecase.GetSortedAppsUseCase
import com.epaperlauncher.feature.launcherhome.domain.usecase.SearchAppsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

/**
 * Hilt module for providing dependencies to the launcher-home feature.
 */
@Module
@InstallIn(ViewModelComponent::class)
object HomeModule {

    @Provides
    @ViewModelScoped
    fun provideGetSortedAppsUseCase(
        appListRepository: AppListRepository
    ): GetSortedAppsUseCase {
        return GetSortedAppsUseCase(appListRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideSearchAppsUseCase(
        appListRepository: AppListRepository
    ): SearchAppsUseCase {
        return SearchAppsUseCase(appListRepository)
    }
}
