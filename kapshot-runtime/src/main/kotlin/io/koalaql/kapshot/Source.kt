package io.koalaql.kapshot

class Source(
    val location: SourceLocation,
    val text: String
) {
    override fun toString(): String = text
}