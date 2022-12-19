package io.koalaql.kapshot

fun <T> addSourceToBlock(block: CapturedBlock<T>, source: String): CapturedBlock<T> = object : CapturedBlock<T> {
    override fun invoke(): T = block()
    override fun source(): String = source
}