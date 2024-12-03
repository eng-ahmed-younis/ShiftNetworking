plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("maven-publish")
}

android {
    namespace = "com.network.networking"
    compileSdk = 35

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    // Core dependencies
    implementation(libs.okhttp)
    implementation(libs.retrofit)
    implementation(libs.gson)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)

    // Chucker Dependency for network logging
    debugImplementation("com.github.chuckerteam.chucker:library:4.0.0") // For debug builds
    releaseImplementation("com.github.chuckerteam.chucker:library-no-op:4.0.0") // For release builds (no-op version)
}

configurations.all {
    resolutionStrategy {
        // If you want to force a specific version of a dependency, you can add it here, but it's not necessary for Chucker
        // force("com.github.chuckerteam.chucker:library:4.0.0")
    }
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("maven") {
                from(components["release"])
                groupId = "com.core"
                artifactId = "shift-networking"
                version = "1.1.1"
            }
        }
    }
}
