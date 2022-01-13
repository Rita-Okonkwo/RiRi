buildscript {
    val kotlin_version by extra("1.5.0")
    repositories {
        gradlePluginPortal()
        jcenter()
        google()
        mavenCentral()
    }
    val kotlinVersion = "1.4.0"
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.0")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.3.5")
        classpath("com.android.tools.build:gradle:4.2.0")
        classpath("org.jetbrains.kotlin:kotlin-serialization:$kotlinVersion")
        classpath("com.google.gms:google-services:4.3.5")
        classpath("com.squareup.sqldelight:gradle-plugin:1.5.1")
    }
}

group = "com.tech.riri"
version = "1.0-SNAPSHOT"

allprojects {
    repositories {
        google()
        jcenter()
        mavenCentral()
    }
}