buildscript {
    val kotlin_version by extra("1.4.32")
    repositories {
        gradlePluginPortal()
        jcenter()
        google()
        mavenCentral()
    }
    val kotlinVersion = "1.4.0"
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.10")
        classpath("com.android.tools.build:gradle:4.1.3")
        classpath("org.jetbrains.kotlin:kotlin-serialization:$kotlinVersion")
        classpath("com.google.gms:google-services:4.3.5")
    }
}

group = "com.example.riri"
version = "1.0-SNAPSHOT"

allprojects {
    repositories {
        google()
        jcenter()
        mavenCentral()
    }
}