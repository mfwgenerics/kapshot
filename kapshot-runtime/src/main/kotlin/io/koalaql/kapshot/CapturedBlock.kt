package io.koalaql.kapshot

fun interface CapturedBlock<T> {
    operator fun invoke(): T

    fun source(): String = error("there is no source code for this block")
}
