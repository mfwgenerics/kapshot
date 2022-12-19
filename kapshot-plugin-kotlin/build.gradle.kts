repositories {
    mavenCentral()
}

plugins {
    id("publish")

    kotlin("jvm") version "1.7.21"
    kotlin("kapt") version "1.7.21"
}

dependencies {
    compileOnly("org.jetbrains.kotlin:kotlin-compiler-embeddable")

    kapt("com.google.auto.service:auto-service:1.0.1")
    compileOnly("com.google.auto.service:auto-service-annotations:1.0.1")
}
