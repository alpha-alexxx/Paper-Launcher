# Icon Engine ProGuard Rules

# Keep WorkManager worker
-keep class com.epaperlauncher.iconengine.worker.** { *; }

# Keep Hilt generated classes
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }

# Keep Room entities
-keep class com.epaperlauncher.core.data.model.IconCacheEntity { *; }
