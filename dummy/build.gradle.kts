plugins {
    id("com.android.library")
}

android {
    namespace = "ro.davidarsene.dummy"
    compileSdk = 33
    buildToolsVersion = "34.0.0"

    defaultConfig {
        minSdk = 31
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        aidl = true
    }
}

dependencies {
    implementation("androidx.annotation:annotation-jvm:+")
}
