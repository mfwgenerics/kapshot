repositories {
    mavenCentral()
}

plugins {
    id("conventions")

    id("java-gradle-plugin")
    kotlin("jvm") version "1.7.21"

    id("com.github.gmazzo.buildconfig") version "3.1.0"
}

buildConfig {
    buildConfigField("String", "VERSION", "\"${project.version}\"")
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
