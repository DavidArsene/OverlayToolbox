plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "ro.davidarsene.leitmotif"
    compileSdk = 34
    buildToolsVersion = "34.0.0"

    defaultConfig {
        minSdk = 31
        targetSdk = 34
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
    compileOnly(project(":dummy"))

    implementation("com.github.topjohnwu.libsu:core:+")
    implementation("com.github.topjohnwu.libsu:service:+")
    implementation("org.lsposed.hiddenapibypass:hiddenapibypass:+")

    implementation("androidx.core:core-ktx:+")
    implementation("com.google.android.material:material:+")
}
