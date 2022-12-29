plugins {
    id("io.koalaql.kapshot-plugin")

    kotlin("jvm") version "1.8.0"
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
