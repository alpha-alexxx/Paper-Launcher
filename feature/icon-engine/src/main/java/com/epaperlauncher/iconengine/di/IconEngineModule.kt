package com.epaperlauncher.iconengine.di

import android.content.Context
import android.content.pm.PackageManager
import androidx.work.WorkManager
import com.epaperlauncher.core.data.local.IconCacheDao
import com.epaperlauncher.iconengine.data.repository.IconRepositoryImpl
import com.epaperlauncher.iconengine.domain.repository.IconRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class IconEngineModule {

    @Binds
    @Singleton
    abstract fun bindIconRepository(impl: IconRepositoryImpl): IconRepository

    companion object {
        @Provides
        @Singleton
        fun providePackageManager(@ApplicationContext context: Context): PackageManager =
            context.packageManager

        @Provides
        @Singleton
        fun provideWorkManager(@ApplicationContext context: Context): WorkManager =
            WorkManager.getInstance(context)
    }
}
