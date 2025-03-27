plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.attendenceapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.attendenceapp"
        minSdk = 24
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    // AndroidX and Material Design libraries
    implementation(libs.appcompat)                          // AndroidX AppCompat
    implementation(libs.material)                           // Material Components
    implementation(libs.activity)                           // AndroidX Activity
    implementation(libs.constraintlayout)                   // ConstraintLayout

    // Firebase libraries
    implementation(libs.firebase.firestore)                 // Firestore (for potential use)
    implementation(libs.firebase.auth)                      // Firebase Authentication
    implementation(libs.firebase.storage)                   // Firebase Storage

    // Add Firebase Realtime Database for student data
//    implementation (com.google.firebase:firebase-database:20.3.0)  // Firebase Realtime Database

    // Navigation Components
    implementation(libs.navigation.fragment)                // Navigation Fragment
    implementation(libs.navigation.ui)
    implementation(libs.firebase.database)
    implementation(libs.constraintlayout.core)
    implementation(libs.camera.view)
    implementation(libs.vision.common)
    implementation(libs.lifecycle.compiler)
    implementation(libs.credentials)
    implementation(libs.credentials.play.services.auth)
    implementation(libs.googleid)                      // Navigation UI

    // Glide for image handling (optional, but useful for image display)
//    implementation 'com.github.bumptech.glide:glide:4.12.0'  // Glide image loader
//    annotationProcessor 'com.github.bumptech.glide:compiler:4.12.0'

    // Unit and Android testing libraries
    testImplementation(libs.junit)                          // JUnit for testing
    androidTestImplementation(libs.ext.junit)               // AndroidX JUnit for testing
    androidTestImplementation(libs.espresso.core)           // Espresso for UI testing
    implementation(libs.camera.core)
    implementation(libs.camera.camera2)
    implementation(libs.camera.lifecycle)
    implementation(libs.camera.view.v100alpha30)
    implementation(libs.camera.extensions)
    implementation(libs.firebase.storage.v2001)
    implementation (libs.firebase.auth.v2200)
    implementation (libs.material.v190)

    implementation (libs.firebase.auth)
    implementation (libs.firebase.database.v2005)

    // Optional Firebase BoM (ensures compatibility between Firebase services)
//    implementation platform("com.google.firebase:firebase-bom:32.1.0") // Firebase Bill of Materials (BoM)

}
