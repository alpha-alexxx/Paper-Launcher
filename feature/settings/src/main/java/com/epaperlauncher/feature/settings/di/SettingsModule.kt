package com.epaperlauncher.feature.settings.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.proto.ProtoSerializer
import com.epaperlauncher.core.data.domain.model.ThemeConfig
import com.epaperlauncher.core.data.domain.model.ThemeConfigProto
import com.epaperlauncher.core.data.domain.repository.BillingRepository
import com.epaperlauncher.core.data.domain.repository.FilterEngineController
import com.epaperlauncher.core.data.domain.repository.IconRepository
import com.epaperlauncher.core.data.domain.repository.ThemeRepository
import com.epaperlauncher.feature.filterengine.data.impl.FilterEngineControllerImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import java.io.IOException

/**
 * Hilt module providing settings screen dependencies.
 */
@Module
@InstallIn(ViewModelComponent::class)
abstract class SettingsModule {

    @Binds
    @ViewModelScoped
    abstract fun bindFilterEngineController(
        impl: FilterEngineControllerImpl
    ): FilterEngineController
}

/**
 * Hilt module providing DataStore instance for theme configuration.
 */
@Module
@InstallIn(ViewModelComponent::class)
object DataStoreModule {

    @Provides
    @ViewModelScoped
    fun provideThemeDataStore(
        @ApplicationContext context: Context
    ): DataStore<ThemeConfigProto> {
        return androidx.datastore.core.DataStoreFactory.create(
            serializer = ThemeConfigProtoSerializer,
            produceFile = { context.filesDir.resolve("theme_config.pb") },
            corruptionHandler = ReplaceFileCorruptionHandler(
                produceNewData = { ThemeConfigProto.getDefaultInstance() }
            ),
            scope = kotlinx.coroutines.CoroutineScope(Dispatchers.IO + kotlinx.coroutines.SupervisorJob())
        )
    }
}

/**
 * Serializer for ThemeConfigProto.
 */
object ThemeConfigProtoSerializer : ProtoSerializer<ThemeConfigProto> {
    override val defaultValue: ThemeConfigProto = ThemeConfigProto.getDefaultInstance()

    override suspend fun readFrom(input: java.io.InputStream): ThemeConfigProto {
        return try {
            ThemeConfigProto.parseFrom(input)
        } catch (e: IOException) {
            throw androidx.datastore.core.CorruptionException("Cannot read proto", e)
        }
    }

    override suspend fun writeTo(t: ThemeConfigProto, output: java.io.OutputStream) {
        t.writeTo(output)
    }
}
