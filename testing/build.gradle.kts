plugins {
    id("io.koalaql.kapshot-plugin")

    kotlin("jvm") version "1.7.21"
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
