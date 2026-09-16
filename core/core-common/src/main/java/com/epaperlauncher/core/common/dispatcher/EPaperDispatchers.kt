package com.epaperlauncher.core.common.dispatcher

import javax.inject.Qualifier

/**
 * Qualifier annotations for coroutine dispatchers
 */
@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class Dispatcher(val epaperDispatcher: EPaperDispatchers)

enum class EPaperDispatchers {
    Default,
    IO,
    Main,
    Unconfined
}
