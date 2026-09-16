import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.*

class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.library")
                apply("org.jetbrains.kotlin.android")
                apply("com.google.devtools.ksp")
            }

            android {
                compileSdk = 34

                defaultConfig {
                    minSdk = 26
                }

                compileOptions {
                    sourceCompatibility = JavaVersion.VERSION_17
                    targetCompatibility = JavaVersion.VERSION_17
                }

                kotlin {
                    jvmToolchain(17)
                }
            }

            dependencies {
                add("implementation", platform(project.libs.findBundle("compose").get()))
                add("implementation", platform(project.libs.findBundle("hilt").get()))
            }
        }
    }
}
