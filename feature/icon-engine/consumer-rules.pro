# Consumer ProGuard Rules for Icon Engine Module
# These rules are applied to apps that consume this library

# Keep public API
-keep class com.epaperlauncher.iconengine.domain.repository.IconRepository { *; }
-keep class com.epaperlauncher.iconengine.domain.model.** { *; }
