package io.koalaql.kapshot

interface Capturable<T : Capturable<T>> {
    val source: Source get() = error("there is no source code for this block")

    fun withSource(source: Source): T
}