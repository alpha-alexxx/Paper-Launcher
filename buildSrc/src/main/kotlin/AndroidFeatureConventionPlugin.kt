import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.*

class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply {
                apply("e.paper.library")
                apply("org.jetbrains.kotlin.plugin.serialization")
            }

            android {
                defaultConfig {
                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                }

                buildTypes {
                    getByName("debug") {
                        isMinifyEnabled = false
                    }
                    getByName("release") {
                        isMinifyEnabled = true
                        proguardFiles(
                            getDefaultProguardFile("proguard-android-optimize.txt"),
                            "proguard-rules.pro"
                        )
                    }
                }

                buildFeatures {
                    compose = true
                }

                composeOptions {
                    kotlinCompilerExtensionVersion = "1.5.14"
                }

                packaging {
                    resources {
                        excludes += "/META-INF/{AL2.0,LGPL2.1}"
                        excludes += "/META-INF/DEPENDENCIES"
                    }
                }
            }

            dependencies {
                add("implementation", project(":core:core-common"))
                add("implementation", project(":core:core-data"))
                add("implementation", project(":core:core-ui"))
                
                add("implementation", libs.findLibrary("compose.material3").get())
                add("implementation", libs.findLibrary("compose.material3.adaptive").get())
                add("implementation", libs.findLibrary("hilt.navigation.compose").get())
                add("implementation", libs.findLibrary("lifecycle.runtime.compose").get())
                add("implementation", libs.findLibrary("navigation.compose").get())
                add("implementation", libs.findLibrary("windowmanager").get())
            }
        }
    }
}
