plugins {
    id("com.palantir.git-version")
}

val gitVersion: groovy.lang.Closure<*> by extra

group = "io.koalaql"
version = gitVersion()

check("$version".isNotBlank() && version != "unspecified")
    { "invalid version $version" }