plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("kotlin-kapt") // 🔹 Added kapt for annotation processing (Room needs this)
}

android {
    namespace = "com.example.facetrackinggame"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.facetrackinggame"
        minSdk = 34
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

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
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.storage)

    // Room Database Dependencies (Added these)
    implementation(libs.androidx.room.runtime) // 🔹 Room database runtime
    implementation(libs.androidx.room.ktx) // 🔹 Room extensions for Kotlin coroutines
    kapt(libs.androidx.room.compiler) // 🔹 Room annotation processor (Required for database generation)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // ML Kit & CameraX (Already present)
    implementation(libs.mlkit.face.detection)
    implementation(libs.camerax.core)
    implementation(libs.camerax.camera2)
    implementation(libs.camerax.lifecycle)
    implementation(libs.camerax.view)
    implementation(libs.coroutines.android)

    // Jetpack Compose UI (Already present)
    implementation(platform(libs.androidx.compose.bom)) // Use Compose BOM for consistent versions
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.material.icons.extended)


    // Debugging Tools (Already present)
    debugImplementation(libs.androidx.ui.tooling)
}
