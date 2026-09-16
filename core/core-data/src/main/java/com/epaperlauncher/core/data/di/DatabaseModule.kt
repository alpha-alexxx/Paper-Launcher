package com.epaperlauncher.core.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.room.Room
import com.epaperlauncher.core.data.data.local.db.EPaperDatabase
import com.epaperlauncher.core.data.data.repository.ThemeRepositoryImpl
import com.epaperlauncher.core.data.domain.model.ThemeConfig
import com.epaperlauncher.core.data.domain.repository.ThemeRepository
import com.epaperlauncher.core.data.local.IconCacheDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideEPaperDatabase(@ApplicationContext context: Context): EPaperDatabase =
        Room.databaseBuilder(
            context,
            EPaperDatabase::class.java,
            "epaper_database"
        )
        .fallbackToDestructiveMigration() // For early development; use proper migrations in production
        .build()

    @Provides
    @Singleton
    fun provideIconCacheDao(database: EPaperDatabase): IconCacheDao =
        database.iconCacheDao()

    @Provides
    @Singleton
    fun provideThemeRepository(
        dataStore: DataStore<ThemeConfig>
    ): ThemeRepository = ThemeRepositoryImpl(dataStore)
}
