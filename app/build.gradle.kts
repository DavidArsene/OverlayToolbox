import com.google.protobuf.gradle.id

plugins {
    id("com.android.application") version "8.3.0-alpha07"
    id("org.jetbrains.kotlin.android") version "+"
    id("com.google.protobuf") version "+"
}

android {
    namespace = "ro.davidarsene.overlaytoolbox"
    compileSdk = 34

    defaultConfig {
        minSdk = 31
        targetSdk = 34
        versionCode = 10000
        versionName = "1.0"
        resourceConfigurations.addAll(listOf("en", "xhdpi"))
    }
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")

            optimization.keepRules {
                ignoreExternalDependencies(
                    "androidx.appcompat:appcompat",
                    "androidx.coordinatorlayout:coordinatorlayout",
                    "androidx.core:core",
                    "androidx.fragment:fragment",
                    "androidx.recyclerview:recyclerview",
                    "androidx.vectordrawable:vectordrawable-animated",
                    "com.google.android.material:material",
                )
            }
        }
    }

    packaging {
        resources.excludes.addAll(listOf("kotlin/**", "kotlin*", "META-INF/**", "DebugProbesKt.bin", "google/**", "src/google/**"))
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

    implementation("androidx.core:core-ktx:+")
    implementation("androidx.recyclerview:recyclerview:+")
    implementation("androidx.activity:activity-ktx:+")
    implementation("com.google.android.material:material:+")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:+")

    implementation("com.google.protobuf:protobuf-kotlin-lite:+")

    implementation("io.github.l4digital:fastscroll:+")
//    implementation("com.github.DavidArsene:arscblamer:+")
    implementation(files("/home/david/AndroidStudioProjects/arscblamer/build/libs/arscblamer.jar"))
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:+"
    }
    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                id("java") {
                    option("lite")
                }
                id("kotlin") {
                    option("lite")
                }
            }
        }
    }
}
