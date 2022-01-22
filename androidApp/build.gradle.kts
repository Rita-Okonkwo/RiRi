import java.util.Properties

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

    // Dependencies for local unit tests
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.hamcrest:hamcrest-all:1.3")
    testImplementation("androidx.test:core-ktx:1.4.0")
    testImplementation("org.robolectric:robolectric:4.3.1")
    testImplementation("androidx.test.ext:junit-ktx:1.1.3")
    testImplementation("androidx.arch.core:core-testing:2.1.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.4.2")


    // AndroidX Test - Instrumented testing
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.0-alpha03")

    implementation("com.github.bumptech.glide:glide:4.11.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.11.0")
}

android {
    compileSdkVersion(31)
    defaultConfig {
        applicationId = "com.tech.riri.androidApp"
        minSdkVersion(21)
        targetSdkVersion(31)
        versionCode = 3
        versionName = "3.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    val prop = Properties()
    prop.load(project.rootProject.file("local.properties").inputStream())
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            buildConfigField("String", "API_KEY", prop.getProperty("API_KEY"))
            buildConfigField("String", "IMAGE_ENDPOINT", prop.getProperty("IMAGE_ENDPOINT"))
            buildConfigField("String", "CONTENT_TYPE", prop.getProperty("CONTENT_TYPE"))
        }
        getByName("debug") {
            isMinifyEnabled = false
            buildConfigField("String", "API_KEY", prop.getProperty("API_KEY"))
            buildConfigField("String", "IMAGE_ENDPOINT", prop.getProperty("IMAGE_ENDPOINT"))
            buildConfigField("String", "CONTENT_TYPE", prop.getProperty("CONTENT_TYPE"))
        }
    }

    compileOptions {
        sourceCompatibility (JavaVersion.VERSION_1_8)
                targetCompatibility (JavaVersion.VERSION_1_8)
    }

    buildFeatures {
        viewBinding =true
    }

    testOptions.unitTests {
        isIncludeAndroidResources = true
    }

}