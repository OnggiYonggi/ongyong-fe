plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("androidx.navigation.safeargs.kotlin")
}

android {
    namespace = "com.bravepeople.onggiyonggi"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.bravepeople.onggiyonggi"
        minSdk = 28
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
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
        dataBinding = true
    }
}

val cameraxVersion = "1.3.0"
dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.navigation.fragment)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //viewmodel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")
    implementation("androidx.fragment:fragment-ktx:1.8.6")
    implementation("androidx.activity:activity-ktx:1.10.1")

    // coil
    implementation("io.coil-kt:coil:2.4.0")

    //camera
    implementation ("androidx.camera:camera-core:$cameraxVersion")
    implementation ("androidx.camera:camera-camera2:$cameraxVersion")
    implementation ("androidx.camera:camera-lifecycle:$cameraxVersion")
    implementation ("androidx.camera:camera-view:$cameraxVersion")
    implementation ("androidx.camera:camera-extensions:$cameraxVersion")

    //Timber
    implementation("com.jakewharton.timber:timber:4.7.1")

    //lottie
    implementation("com.airbnb.android:lottie:6.1.0")
}