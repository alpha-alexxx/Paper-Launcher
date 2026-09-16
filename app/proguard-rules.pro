# E-Paper Launcher ProGuard Rules

# Keep model classes for serialization
-keepclassmembers class com.epaperlauncher.core.data.domain.model.** {
    *;
}

# Hilt
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ComponentSupplier { *; }

# Room
-keep class * extends androidx.room.RoomDatabase { *; }
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# Kotlin Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# AGSL Shaders (API 33+)
-dontwarn android.graphics.RuntimeShader
-dontwarn android.graphics.Shader

# MediaProjection
-dontwarn android.media.projection.**
