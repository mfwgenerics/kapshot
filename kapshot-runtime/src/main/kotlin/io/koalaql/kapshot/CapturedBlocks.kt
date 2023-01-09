package io.koalaql.kapshot

fun <T : Capturable<T>> addSourceToBlock(
    block: Capturable<T>,
    location: String,
    source: String
): T {
    fun parseOffset(offset: String): SourceOffset = offset
        .splitToSequence(",")
        .take(3)
        .map { it.toInt() }
        .toList()
        .let {
            SourceOffset(
                char = it[0],
                line = it[1],
                column = it[2]
            )
        }

    val parsedLocation = location
        .splitToSequence("\n")
        .take(3)
        .toList()
        .let {
            SourceLocation(
                path = it[0],
                from = parseOffset(it[1]),
                to = parseOffset(it[2])
            )
        }

    return block.withSource(
        Source(parsedLocation, source)
    )
}