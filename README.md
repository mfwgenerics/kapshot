# Kapshot
Kapshot is a simple Kotlin compiler plugin for capturing source code text from closures.

## Usage

Include the `io.koalaql.kapshot-plugin` Gradle plugin in your `build.gradle.kts`:

```kotlin
plugins {
    /* ... */

    id("io.koalaql.kapshot-plugin") version "0.0.2"
}
```

Now your Kotlin code can use `CapturedBlock<T>` as a source enriched replacement for `() -> T`.
You can call `source()` on any instance of
`CapturedBlock` to access the source text for that block.

```kotlin
import io.koalaql.kapshot.CapturedBlock

val captured = CapturedBlock {
    println("Hello!")
}

check(captured.source() == """println("Hello!")""")
```

You can invoke the block similar to a regular function: 

```kotlin
import io.koalaql.kapshot.CapturedBlock

fun equation(block: CapturedBlock<Int>): String {
    val result = block() // invoke the block

    return "${block.source()} = $result"
}

check(equation { 2 + 2 } == "2 + 2 = 4")
```

## Purpose

The purpose of this plugin is to support experimental literate
programming and documentation generation techniques in Kotlin.

An example of this is the code used to generate this README.md.
Capturing source from blocks allows sample code to be run and
tested during generation.

View the source here: [readme/src/main/kotlin/Main.kt](readme/src/main/kotlin/Main.kt)

## Parameterized Blocks

The default `CapturedBlock` interface doesn't accept any
arguments to `invoke` and is only generic on the return type. This
means the captured source block must depend only on state from the
enclosing scope. To write source capturing versions of builder blocks
or common higher-order functions like `map` and `filter` you will
need to define your own capture interface that extends `Capturable`.

```kotlin
/* must be a fun interface to support SAM conversion from blocks */
fun interface CustomCapturable<T, R> : Capturable<CustomCapturable<T, R>> {
    /* invoke is not special. this could be any single abstract method */
    operator fun invoke(arg: T): R

    /* withSource is called by the plugin to add source information */
    override fun withSource(source: String): CustomCapturable<T, R> =
        object : CustomCapturable<T, R> by this { override fun source(): String = source }
}
```

Once you have declared your own `Capturable` you can use it
in a similar way to `CapturedBlock` from above.

```kotlin
fun <T> List<T>.mapped(block: CustomCapturable<T, T>): String {
    return "$this.map { ${block.source()} } = ${map { block(it) }}"
}

check(
    listOf(1, 2, 3).mapped { x -> x*2 } ==
    "[1, 2, 3].map { x -> x*2 } = [2, 4, 6]"
)
```

If it is present, the block's argument list is considered part of its source text.