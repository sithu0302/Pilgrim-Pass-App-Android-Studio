plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.pilgrimpass"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.pilgrimpass"
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
}

dependencies {

    // AndroidX Libraries (ensure these are up-to-date in libs.versions.toml)
    implementation(libs.appcompat)
    implementation(libs.material) // One instance is enough
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // Google Play Services Maps (ensure this is up-to-date in libs.versions.toml)
    implementation(libs.play.services.maps) // Use this, assuming it points to the latest version

    // Testing Libraries
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Zxing Libraries for QR code scanning/generation
    // Note: zxing-android-embedded usually brings in zxing-core as a transitive dependency.
    // However, if you explicitly need zxing-core for direct usage, keep it.
    implementation(libs.zxing.core)
    implementation(libs.zxing.android.embedded)
    implementation (libs.material.v1xx)
    // REMOVED DUPLICATE/OLDER VERSIONS:
    // implementation (libs.appcompat.v161) - Replaced by libs.appcompat
    // implementation (libs.material) - Duplicate, already above
    // implementation (libs.play.services.maps.v1810) - Replaced by libs.play.services.maps


    implementation (libs.play.services.location) // For location services (if needed)
    }
