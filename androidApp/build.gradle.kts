plugins {
    id("com.android.application")
    kotlin("android")
    id("com.google.gms.google-services")
    id("kotlin-android")
    id("androidx.navigation.safeargs.kotlin")

}

group = "com.tech.riri"
version = "1.0-SNAPSHOT"

repositories {
    gradlePluginPortal()
    mavenCentral()
    google()
    jcenter()
}

dependencies {
    implementation(project(":shared"))
    compileOnly("io.realm.kotlin:library:0.4.1")
    implementation("com.google.android.material:material:1.3.0")
    implementation("androidx.appcompat:appcompat:1.2.0")
    implementation("androidx.constraintlayout:constraintlayout:2.0.4")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.4.1")
    implementation("androidx.core:core-ktx:1.3.2")
    implementation("io.ktor:ktor-client-android:1.4.0")
    implementation(platform("com.google.firebase:firebase-bom:27.1.0"))
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-storage-ktx")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:${rootProject.extra["kotlin_version"]}")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.3.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.3.1")
    implementation("androidx.navigation:navigation-fragment-ktx:2.3.5")
    implementation("androidx.navigation:navigation-ui-ktx:2.3.5")
    implementation("androidx.core:core-splashscreen:1.0.0-alpha01")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0-alpha05")
    androidTestImplementation("androidx.test:rules:1.3.0-beta01")
    implementation("com.github.bumptech.glide:glide:4.11.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.11.0")
}

android {
    compileSdkVersion(31)
    defaultConfig {
        applicationId = "com.tech.riri.androidApp"
        minSdkVersion(21)
        targetSdkVersion(31)
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility (JavaVersion.VERSION_1_8)
                targetCompatibility (JavaVersion.VERSION_1_8)
    }

    buildFeatures {
        viewBinding =true
    }
}