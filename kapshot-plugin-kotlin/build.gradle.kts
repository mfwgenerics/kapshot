repositories {
    mavenCentral()
}

plugins {
    id("publish")

    kotlin("jvm") version "1.8.0"
    kotlin("kapt") version "1.8.0"
}

dependencies {
    compileOnly("org.jetbrains.kotlin:kotlin-compiler-embeddable")

    kapt("com.google.auto.service:auto-service:1.0.1")
    compileOnly("com.google.auto.service:auto-service-annotations:1.0.1")
}
