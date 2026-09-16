package com.epaperlauncher.feature.billing.di

import android.content.Context
import com.epaperlauncher.core.data.domain.repository.BillingRepository
import com.epaperlauncher.feature.billing.data.BillingRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for providing billing dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class BillingModule {

    @Binds
    @Singleton
    abstract fun bindBillingRepository(
        impl: BillingRepositoryImpl
    ): BillingRepository

    companion object {
        @Provides
        @Singleton
        fun provideBillingRepository(): BillingRepositoryImpl {
            return BillingRepositoryImpl()
        }
    }
}
