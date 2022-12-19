repositories {
    mavenCentral()
}

plugins {
    id("conventions")

    kotlin("jvm") version "1.7.21"

    `maven-publish`
}

publishing {
    publications {
        create<MavenPublication>("default") {
            from(components["java"])
        }
    }
}