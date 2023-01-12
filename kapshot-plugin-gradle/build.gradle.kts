plugins {
    id("conventions")

    id("java-gradle-plugin")
    id("com.gradle.plugin-publish") version "1.1.0"

    id("com.github.gmazzo.buildconfig") version "3.1.0"
}

buildConfig {
    buildConfigField("String", "VERSION", "\"${project.version}\"")
}

dependencies {
    implementation(kotlin("gradle-plugin-api"))
}

pluginBundle {
    website = "https://github.com/mfwgenerics/kapshot"
    vcsUrl = "https://github.com/mfwgenerics/kapshot.git"
    tags = listOf("kotlin", "kapshot", "jvm")
}

gradlePlugin {
    plugins {
        create("kapshotPlugin") {
            id = "io.koalaql.kapshot-plugin"
            displayName = "Kapshot Plugin"
            description = "Kotlin Compiler Plugin for source capture in closure blocks"
            implementationClass = "io.koalaql.kapshot.GradlePlugin"
        }
    }
}
