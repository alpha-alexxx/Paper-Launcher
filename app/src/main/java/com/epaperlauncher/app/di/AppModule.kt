package com.epaperlauncher.app.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.epaperlauncher.core.data.domain.model.ThemeConfig
import com.epaperlauncher.core.data.domain.model.ThemeConfigProtoSerializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private const val THEME_FILE_NAME = "theme_config.pb"

val Context.themeDataStore: DataStore<ThemeConfig> by dataStore(
    fileName = THEME_FILE_NAME,
    serializer = ThemeConfigProtoSerializer
)

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideThemeDataStore(
        @ApplicationContext context: Context
    ): DataStore<ThemeConfig> {
        return context.themeDataStore
    }
}
