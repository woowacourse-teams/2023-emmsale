import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id("org.jetbrains.kotlin.android")
    id("com.android.application") version "8.0.2"
    kotlin("plugin.serialization") version "1.8.21"
    id("kotlin-kapt")
    id("com.google.firebase.crashlytics")
    id("com.google.gms.google-services")
    id("kotlin-parcelize")
}

android {
    namespace = "com.emmsale"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.emmsale"
        minSdk = 28
        targetSdk = 33
        versionCode = 2
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField(
            "String",
            "GITHUB_CLIENT_ID",
            getApiKey("GH_CLIENT_ID"),
        )
        signingConfig = signingConfigs.getByName("debug")
    }
    buildFeatures {
        buildConfig = true
    }
    buildTypes {
        debug {
            buildConfigField("String", "BASE_URL", "\"https://kerdy.kro.kr\"")
        }
        release {
            isShrinkResources = true
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            buildConfigField("String", "BASE_URL", "\"https://prod.kerdy.kro.kr\"")
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    dataBinding {
        enable = true
    }
    tasks.withType(Test::class) {
        testLogging {
            events.addAll(
                arrayOf(
                    org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED,
                    org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED,
                    org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED,
                ),
            )
        }
    }
    testOptions {
        unitTests.isReturnDefaultValues = true
    }
}

fun getApiKey(propertyKey: String): String {
    return gradleLocalProperties(rootDir).getProperty(propertyKey)
}

dependencies {

    // Android
    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.browser:browser:1.5.0")
    implementation("androidx.work:work-runtime-ktx:2.8.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // ViewModel
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    implementation("androidx.fragment:fragment-ktx:1.6.1")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")

    // Kotlinx-Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:1.0.0")

    // OkHttp3
    implementation("com.squareup.okhttp3:logging-interceptor:4.8.0")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("com.squareup.okhttp3:mockwebserver:4.11.0")

    // Glide
    implementation("com.github.bumptech.glide:glide:4.15.1")

    // SwipeRefresh
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:32.2.0"))
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-messaging-ktx")
    implementation("com.google.firebase:firebase-crashlytics-ktx")

    testImplementation("org.junit.jupiter", "junit-jupiter", "5.8.2")
    testImplementation("io.mockk:mockk-android:1.13.5")
    testImplementation("io.mockk:mockk-agent:1.13.5")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")

    // junit4
    testImplementation("junit:junit:4.13.2")
    testImplementation("androidx.test.ext:junit:1.1.5")
    testImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")

    // assertJ
    testImplementation("org.assertj:assertj-core:3.22.0")

    // android-test
    testImplementation("androidx.arch.core:core-testing:2.2.0")

    // recyclerView
    implementation("androidx.recyclerview:recyclerview:1.3.1")

    // SplashScreen
    implementation("androidx.core:core-splashscreen:1.0.0-beta01")

    // imageview
    implementation("de.hdodenhof:circleimageview:3.1.0")
}
