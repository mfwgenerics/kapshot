package io.koalaql.kapshot

interface Capturable<T : Capturable<T>> {
    fun source(): Source = error("there is no source code for this block")

    fun withSource(source: Source): T
}