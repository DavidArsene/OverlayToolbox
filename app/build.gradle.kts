plugins {
    id("com.android.application") version "8.1.0-rc01"
    id("org.jetbrains.kotlin.android") version "1.9.0"
}

android {
    namespace = "ro.davidarsene.leitmotif"
    compileSdk = 33
    buildToolsVersion = "33.0.1"

    defaultConfig {
        minSdk = 31
        targetSdk = 33
        versionCode = 10000
        versionName = "1.0"
        resourceConfigurations.add("en")
    }
    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions.jvmTarget = "11"

    buildFeatures {
        aidl = true
        viewBinding = true
    }
}

dependencies {
    implementation("com.github.topjohnwu.libsu:core:+")
    implementation("com.github.topjohnwu.libsu:service:+")
    implementation("org.lsposed.hiddenapibypass:hiddenapibypass:+")

    implementation("androidx.core:core-ktx:1.10.1")
    implementation("com.google.android.material:material:1.9.0")
}
