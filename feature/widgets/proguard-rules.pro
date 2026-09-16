# Widgets module ProGuard rules

# Keep model classes for serialization
-keep class com.epaperlauncher.feature.widgets.domain.model.** { *; }

# Keep repository implementations
-keep class com.epaperlauncher.feature.widgets.data.repository.** { *; }

# Hilt generated classes
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
