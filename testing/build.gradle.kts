plugins {
    id("io.koalaql.kapshot-plugin")

    kotlin("jvm") version "1.7.21"
}

group = "io.koalaql"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
