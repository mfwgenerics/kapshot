package io.koalaql.kapshot

interface Capturable<T : Capturable<T>> {
    fun source(): String = error("there is no source code for this block")

    fun withSource(source: String): T
}