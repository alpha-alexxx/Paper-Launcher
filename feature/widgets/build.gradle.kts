plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.epaperlauncher.feature.widgets"
    compileSdk = 35

    defaultConfig {
        minSdk = 26
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }
}

dependencies {
    implementation(project(":core:core-data"))
    implementation(project(":core:core-ui"))
    implementation(project(":core:core-common"))

    // Compose
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.material3)
    implementation(libs.compose.material3.adaptive)
    implementation(libs.navigation.compose)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // WorkManager for widget data refresh
    implementation(libs.workmanager.ktx)

    // Coroutines
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)

    // DataStore
    implementation(libs.datastore.core)

    // Serialization for proto
    implementation(libs.kotlinx.serialization.json)

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.coroutines.test)
}
