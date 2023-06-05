package io.koalaql.kapshot

@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY
)
annotation class CaptureSource(
    val location: String = "",
    val text: String = ""
)
