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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
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
    implementation (libs.library )// Replace with the latest version
    debugImplementation (libs.library)
    releaseImplementation (libs.library.no.op)

}

afterEvaluate {

    publishing{
        publications {
            create<MavenPublication>("maven") {
                from(components["release"])
                //com.github.<<your GitHub user name>>
                groupId = "com.github.eng-ahmed-younis"
                //name of your library.
                artifactId = "shift-networking"
                version = "1.0"
            }
        }
    }
}