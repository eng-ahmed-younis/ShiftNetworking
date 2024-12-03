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

    implementation(libs.okhttp)
    implementation(libs.retrofit)
    implementation(libs.gson)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)


    // Chucker Dependency for network logging
    implementation(libs.library)// Replace with the latest version
    debugImplementation(libs.library)
    releaseImplementation(libs.library.no.op)

    dependencies {
        implementation("com.github.chuckerteam.chucker:library:4.0.0") {
            exclude(group = "com.github.chuckerteam.chucker", module = "library")
        }
    }


}

configurations.all {
    resolutionStrategy {

        force("com.github.chuckerteam.chucker:library:4.0.0")
    }
}


afterEvaluate {

    publishing {
        publications {
            create<MavenPublication>("maven") {
                from(components["release"])
                //com.github.<<your GitHub user name>>
                groupId = "com.core"
                //name of your library.
                artifactId = "shift-networking"
                version = "1.1.0"
            }
        }
    }
}