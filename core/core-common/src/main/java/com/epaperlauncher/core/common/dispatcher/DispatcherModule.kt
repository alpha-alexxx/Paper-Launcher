package com.epaperlauncher.core.common.dispatcher

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * Hilt module providing coroutine dispatchers
 */
@Module
@InstallIn(SingletonComponent::class)
object DispatcherModule {

    @Provides
    @Dispatcher(EPaperDispatchers.Default)
    fun provideDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

    @Provides
    @Dispatcher(EPaperDispatchers.IO)
    fun provideIODispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @Dispatcher(EPaperDispatchers.Main)
    fun provideMainDispatcher(): CoroutineDispatcher = Dispatchers.Main

    @Provides
    @Dispatcher(EPaperDispatchers.Unconfined)
    fun provideUnconfinedDispatcher(): CoroutineDispatcher = Dispatchers.Unconfined
}
