plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.example.nurdor_volunteer_app_v3"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.nurdor_volunteer_app_v3"
        minSdk = 29
        targetSdk = 36
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
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Retrofit for HTTP requests
    implementation(libs.retrofit)
    implementation(libs.converter.gson) // Gson converter

    // GSON converter for Retrofit
    implementation(libs.converter.gson)

    // Kotlin Coroutines for asynchronous programming
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.core)

    // Room library
    implementation(libs.androidx.room.runtime)
    // additional coroutines support for Room
    implementation(libs.androidx.room.ktx)

    // implementation(libs.androidx.room.compiler)
    ksp(libs.androidx.room.compiler)

    // lifecycle ViewModel KTX Dependency
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    // bcrypt for passwords encryption
    implementation(libs.jbcrypt)

    // ViewPager2
    implementation(libs.androidx.viewpager2)

    // material components
    implementation (libs.material)

    // Glide lib for images
    implementation(libs.glide) // or the latest version
    annotationProcessor(libs.compiler)

    implementation(libs.compose.material)

    // Open Street Map
    //implementation(libs.osmdroid.parent.x.x.x0)
    implementation(libs.osmdroid.android)

    implementation(libs.androidx.fragment.ktx)

    implementation(libs.androidx.work.runtime.ktx)
}