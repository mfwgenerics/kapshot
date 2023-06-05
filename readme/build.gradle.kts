plugins {
    kotlin("jvm")

    id("io.koalaql.kapshot-plugin")
    id("io.koalaql.markout") version "0.0.6"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.koalaql:markout:0.0.6")
}

markout {
    mainClass = "MainKt"
}
