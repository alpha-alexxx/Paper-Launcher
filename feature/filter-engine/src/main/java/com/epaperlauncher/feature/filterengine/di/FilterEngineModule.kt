package com.epaperlauncher.feature.filterengine.di

import android.content.Context
import com.epaperlauncher.core.data.domain.repository.FilterEngineController
import com.epaperlauncher.feature.filterengine.data.impl.FilterEngineControllerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.core.HiltRoot
import javax.inject.Singleton

/**
 * Hilt module providing filter engine dependencies.
 */
@Module
@InstallIn(ServiceComponent::class)
abstract class FilterEngineModule {

    @Binds
    @Singleton
    abstract fun bindFilterEngineController(
        impl: FilterEngineControllerImpl
    ): FilterEngineController
}

/**
 * Entry point for accessing dependencies from the AccessibilityService.
 * Since services are system-instantiated, we can't use constructor injection directly.
 */
@HiltRoot
interface ServiceEntryPoint {
    fun getFilterEngineController(): FilterEngineController
}
