plugins {
    id("io.koalaql.kapshot-plugin")

    kotlin("jvm") version "1.8.0"

    application
}

repositories {
    mavenCentral()
}

application {
    mainClass.set("MainKt")
}

dependencies {
    implementation("io.koalaql:markout:0.0.5")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}