import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

repositories {
    mavenCentral()
}

plugins {
    kotlin("jvm")

    id("com.palantir.git-version")
}

val gitVersion: groovy.lang.Closure<*> by extra

group = "io.koalaql"
version = gitVersion()

check("$version".isNotBlank() && version != "unspecified")
    { "invalid version $version" }

java {
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}