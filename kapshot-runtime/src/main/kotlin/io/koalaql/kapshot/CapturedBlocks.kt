package io.koalaql.kapshot

fun <T : Capturable<T>> addSourceToBlock(block: Capturable<T>, source: String): T =
    block.withSource(source)