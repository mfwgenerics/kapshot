package io.koalaql.kapshot

fun interface CapturedBlock<T> {
    operator fun invoke(): T

    fun source(): String = TODO()
}
