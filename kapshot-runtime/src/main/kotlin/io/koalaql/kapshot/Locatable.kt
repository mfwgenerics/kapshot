package io.koalaql.kapshot

interface Locatable<T : Locatable<T>> {
    fun location(): SourceLocation = error("there is no source location for this block")

    fun withLocation(location: SourceLocation): T
}