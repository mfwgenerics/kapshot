package io.koalaql.kapshot

import kotlin.reflect.*

private fun sourceOf(annotations: List<Annotation>, name: () -> String): Source {
    val anno = annotations
        .singleOrNull { it.annotationClass == CaptureSource::class }
        ?: error("sourceOf call on ${name()} requires the CaptureSource annotation")

    anno as CaptureSource

    check(anno.text.isNotBlank()) { "missing source text" }

    return Source(
        location = parseLocation(anno.location),
        text = anno.text
    )
}

fun sourceOf(type: KType): Source {
    val classifier = checkNotNull(type.classifier as? KClass<*>) { "$type is not a supported target of source capture"}

    return sourceOf(classifier.annotations) { "$type" }
}

inline fun <reified T : Any> sourceOf() = sourceOf(typeOf<T>())

fun sourceOf(method: KFunction<*>): Source =
    sourceOf(method.annotations) { method.name }

fun sourceOf(property: KProperty<*>): Source =
    sourceOf(property.annotations) { property.name }