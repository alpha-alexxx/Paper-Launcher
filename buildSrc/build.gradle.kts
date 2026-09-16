import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    `kotlin-dsl`
}

repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(libs.plugins.android.application.get().pluginId)
    implementation(libs.plugins.android.library.get().pluginId)
    implementation(libs.plugins.kotlin.android.get().pluginId)
    implementation(libs.plugins.kotlin.jvm.get().pluginId)
    implementation(libs.plugins.hilt.get().pluginId)
    implementation(libs.plugins.ksp.get().pluginId)
    implementation(libs.plugins.room.get().pluginId)
}

gradlePlugin {
    plugins {
        register("androidLibrary") {
            id = "e.paper.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("androidFeature") {
            id = "e.paper.feature"
            implementationClass = "AndroidFeatureConventionPlugin"
        }
    }
}
