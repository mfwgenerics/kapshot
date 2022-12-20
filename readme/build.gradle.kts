plugins {
    id("io.koalaql.kapshot-plugin")

    kotlin("jvm") version "1.7.21"

    application
}

repositories {
    mavenCentral()
}

application {
    mainClass.set("MainKt")
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}