import org.gradle.kotlin.dsl.implementation

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.22" // Use your Kotlin version
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
}

kapt {
    correctErrorTypes = true
    useBuildCache = false
//    arguments {
//        arg("dagger.fastInit", "enabled")
//        arg("dagger.hilt.android.internal.disableAndroidSuperclassValidation", "true")
//    }
}
android {
    namespace = "com.example.foodmark"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.foodmark"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11

    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    // Jetpack Compose & Android
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation ("androidx.compose.material:material-icons-extended")
    implementation ("com.google.android.material:material:1.9.0")
    implementation(libs.androidx.material3.android)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.datastore.core.android)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Supabase (via BOM)
    implementation(platform("io.github.jan-tennert.supabase:bom:3.1.4"))
    implementation("io.github.jan-tennert.supabase:supabase-kt")
    implementation("io.github.jan-tennert.supabase:auth-kt")
    implementation("io.github.jan-tennert.supabase:postgrest-kt")
    implementation("io.github.jan-tennert.supabase:storage-kt")
    implementation("androidx.navigation:navigation-compose:2.7.7") // use latest version
    implementation("io.coil-kt:coil-compose:2.4.0")
    implementation("io.github.jan-tennert.supabase:gotrue-kt:2.0.0")

    // Google Identity / Credentials API
    implementation("androidx.credentials:credentials:1.6.0-alpha01")
    implementation("androidx.credentials:credentials-play-services-auth:1.6.0-alpha01")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.1")

    // Ktor HTTP Client
    implementation("io.ktor:ktor-client-android:3.1.3")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // Jetpack ViewModel with Compose
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")

    // Dagger Hilt setup
//    implementation("com.google.dagger:hilt-android:2.53.1")
//    kapt("com.google.dagger:hilt-android-compiler:2.53.1")
//    implementation("androidx.hilt:hilt-navigation-fragment:1.2.0")
//    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
//    kapt("androidx.hilt:hilt-compiler:1.2.0")

    // Check Maven Central or the official Hilt docs for the absolute latest versions
    val hiltVersion = "2.54"
    val hiltAndroidXVersion = "1.2.0" // Keep the same or update if a newer version is available

    implementation("com.google.dagger:hilt-android:$hiltVersion")
    kapt("com.google.dagger:hilt-android-compiler:$hiltVersion")
    implementation("androidx.hilt:hilt-navigation-fragment:$hiltAndroidXVersion")
    implementation("androidx.hilt:hilt-navigation-compose:$hiltAndroidXVersion")
    kapt("androidx.hilt:hilt-compiler:$hiltAndroidXVersion")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.okhttp3:okhttp:5.0.0-alpha.3")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    implementation("com.google.accompanist:accompanist-systemuicontroller:0.30.1")
//    implementation("com.google.accompanist:accompanist-permissions:0.33.0")


    //

    implementation ("androidx.activity:activity-compose:1.8.2")
    implementation("com.google.maps.android:maps-compose:4.1.1")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.6.4")
    implementation ("com.google.android.gms:play-services-location:21.0.1")
}
