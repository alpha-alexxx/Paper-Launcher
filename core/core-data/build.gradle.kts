plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.room)
}

android {
    namespace = "com.epaperlauncher.core.data"
    compileSdk = 34

    defaultConfig {
        minSdk = 26
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
}

dependencies {
    implementation(project(":core:core-common"))

    // Hilt
    implementation(libs.bundles.hilt)
    ksp(libs.hilt.compiler)

    // Room
    implementation(libs.bundles.room)
    ksp(libs.room.compiler)

    // DataStore
    implementation(libs.bundles.datastore)

    // Coroutines
    implementation(libs.bundles.coroutines)

    // Serialization
    implementation(libs.kotlinx.serialization.json)

    // WorkManager (for icon processing)
    implementation(libs.workmanager.ktx)

    // Billing
    implementation(libs.play.billing)

    // Core Android
    implementation(libs.core.ktx)

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.turbine)
}

room {
    schemaDirectory("$projectDir/schemas")
}
