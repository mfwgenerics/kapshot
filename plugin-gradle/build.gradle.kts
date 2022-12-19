repositories {
    mavenCentral()
}

plugins {
    id("java-gradle-plugin")
    kotlin("jvm") version "1.7.10"
}

dependencies {
    implementation(kotlin("gradle-plugin-api"))
}

gradlePlugin {
    plugins {
        create("kapshotPlugin") {
            id = "io.koalaql.kapshot-plugin"
            displayName = "Kapshot Plugin"
            implementationClass = "io.koalaql.kapshot.GradlePlugin"
        }
    }
}
