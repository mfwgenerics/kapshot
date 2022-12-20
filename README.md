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
    return "${block.source()} = ${block()}"
}

check(equation { 2 + 2 } == "2 + 2 = 4")
```

## Purpose

The purpose of this plugin is to support experimental literate
programming and documentation generation techniques using the Kotlin language.