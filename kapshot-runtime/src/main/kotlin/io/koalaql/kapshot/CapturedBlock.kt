package io.koalaql.kapshot

fun interface CapturedBlock<T>: Capturable<CapturedBlock<T>> {
    operator fun invoke(): T

    override fun withSource(source: String): CapturedBlock<T> {
        return object : CapturedBlock<T> by this {
            override fun source(): String = source
        }
    }
}
