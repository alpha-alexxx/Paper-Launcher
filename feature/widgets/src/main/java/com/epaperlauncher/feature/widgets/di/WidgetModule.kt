package com.epaperlauncher.feature.widgets.di

import android.content.Context
import com.epaperlauncher.feature.widgets.data.repository.WidgetRepositoryImpl
import com.epaperlauncher.feature.widgets.domain.repository.WidgetRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped

/**
 * Hilt module for widget feature dependencies
 */
@Module
@InstallIn(ViewModelComponent::class)
abstract class WidgetModule {

    /**
     * Bind the WidgetRepository implementation
     */
    @Binds
    @ViewModelScoped
    abstract fun bindWidgetRepository(
        impl: WidgetRepositoryImpl
    ): WidgetRepository
}
