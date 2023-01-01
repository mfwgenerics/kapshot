package io.koalaql.kapshot

import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KType
import kotlin.reflect.typeOf

private fun sourceOf(annotations: List<Annotation>, name: () -> String): String {
    val anno = annotations
        .singleOrNull { it.annotationClass == CaptureSource::class }
        ?: error("sourceOf call on ${name()} requires the CaptureSource annotation")

    anno as CaptureSource

    check(anno.text.isNotBlank()) { "missing source text" }

    return anno.text
}

fun sourceOf(type: KType): String {
    val classifier = checkNotNull(type.classifier as? KClass<*>) { "$type is not a supported target of source capture"}

    return sourceOf(classifier.annotations) { "$type" }
}

inline fun <reified T : Any> sourceOf() = sourceOf(typeOf<T>())

fun sourceOf(method: KFunction<*>): String =
    sourceOf(method.annotations) { method.name }