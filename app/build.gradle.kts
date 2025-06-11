import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

val naverClientId = gradleLocalProperties(rootDir, providers).getProperty("naver.client.id")
val googleMapsAPIKey= gradleLocalProperties(rootDir, providers).getProperty("google.maps.api.key")

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("androidx.navigation.safeargs.kotlin")
    id("kotlin-parcelize")
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.22"
    id("kotlin-kapt")
    id("com.google.devtools.ksp") version "2.1.10-1.0.31"
    id("com.google.dagger.hilt.android") version "2.51"
}

android {
    namespace = "com.bravepeople.onggiyonggi"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.bravepeople.onggiyonggi"
        minSdk = 28
        targetSdk = 35
        versionCode = 2
        versionName = "1.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField(
            "String",
            "NAVER_CLIENT_ID",
            gradleLocalProperties(rootDir, providers).getProperty("naver.client.id")
        )

        buildConfigField(
            "String",
            "NAVER_API_CLIENT_ID",
            gradleLocalProperties(rootDir, providers).getProperty("naver.api.client.id")
        )

        buildConfigField(
            "String",
            "NAVER_API_CLIENT_SECRET",
            gradleLocalProperties(rootDir, providers).getProperty("naver.api.client.secret")
        )

        buildConfigField(
            "String",
            "GOOGLE_MAPS_API_KEY",
            gradleLocalProperties(rootDir, providers).getProperty("google.maps.api.key")
        )

        buildConfigField(
            "String",
            "BASE_URL",
            gradleLocalProperties(rootDir, providers).getProperty("base.url")
        )

        buildConfigField(
            "String",
            "NAVER_MAP_URL",
            gradleLocalProperties(rootDir, providers).getProperty("naver.map.url")
        )

        buildConfigField(
            "String",
            "GOOGLE_MAPS_URL",
            gradleLocalProperties(rootDir, providers).getProperty("google.maps.url")
        )

        manifestPlaceholders["NAVER_CLIENT_ID",] = naverClientId

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
    implementation(libs.play.services.location)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //javapoet
    implementation("com.squareup:javapoet:1.13.0")

    //retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:1.0.0")

    // define a BOM and its version
    implementation(platform("com.squareup.okhttp3:okhttp-bom:4.10.0"))

    // define any required OkHttp artifacts without version
    implementation("com.squareup.okhttp3:okhttp")
    implementation("com.squareup.okhttp3:logging-interceptor")

    //viewmodel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")
    implementation("androidx.fragment:fragment-ktx:1.8.6")
    implementation("androidx.activity:activity-ktx:1.10.1")

    // coil
    implementation("io.coil-kt:coil:2.7.0")
    implementation("io.coil-kt.coil3:coil-compose:3.1.0")

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

    //hilt
    implementation("com.google.dagger:hilt-android:2.48")
    kapt("com.google.dagger:hilt-android-compiler:2.46.1")
    kapt("com.google.dagger:dagger-android-processor:2.46.1")

    //coroutine
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")

    // 네이버 지도 SDK
    implementation("com.naver.maps:map-sdk:3.21.0")

    //xml converter
    implementation ("com.squareup.retrofit2:converter-simplexml:2.9.0")

    //fab (floating action button)
    implementation ("com.google.android.material:material:1.11.0")

    // Glide (이미지 로딩 라이브러리)
    implementation("com.github.bumptech.glide:glide:4.16.0")
    kapt("com.github.bumptech.glide:compiler:4.16.0")

    // 이미지 crop
    implementation ("com.github.yalantis:ucrop:2.2.8")

    // skeleton ui
    implementation ("com.facebook.shimmer:shimmer:0.5.0")
}

kapt {
    correctErrorTypes = true
}

hilt {
    enableAggregatingTask = false
}