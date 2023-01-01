package io.koalaql.kapshot

@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.FUNCTION
)
annotation class CaptureSource(
    val text: String = ""
)
