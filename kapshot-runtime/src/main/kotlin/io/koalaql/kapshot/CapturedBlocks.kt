package io.koalaql.kapshot

fun <T : Capturable<T>> addSourceToBlock(
    block: Capturable<T>,
    location: String,
    source: String
): T = block.withSource(
    Source(parseLocation(location), source)
)