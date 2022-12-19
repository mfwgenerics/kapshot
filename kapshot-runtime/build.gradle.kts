repositories {
    mavenCentral()
}

plugins {
    kotlin("jvm") version "1.7.10"

    `maven-publish`
}

group = "io.koalaql"
version = "0.0.1"

publishing {
    publications {
        create<MavenPublication>("default") {
            from(components["java"])
        }
    }
}