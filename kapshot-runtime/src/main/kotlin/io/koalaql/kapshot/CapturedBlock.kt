package io.koalaql.kapshot

fun interface CapturedBlock<T>: Capturable<CapturedBlock<T>> {
    operator fun invoke(): T

    override fun withSource(source: Source): CapturedBlock<T> {
        return object : CapturedBlock<T> by this {
            override val source = source
        }
    }
}
