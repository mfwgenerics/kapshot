repositories {
    mavenCentral()
}

plugins {
    id("publish")

    kotlin("jvm") version "1.8.0"
}

dependencies {
    api(kotlin("reflect"))
}