import io.koalaql.kapshot.CapturedBlock
import kotlin.io.path.Path
import kotlin.io.path.writeText

fun execSource(block: CapturedBlock<*>): String {
    block()
    return block.source()
}

fun main() {
    val blockClass = CapturedBlock::class
    val importStatement = "import ${blockClass.qualifiedName}"

    Path("../README.md").writeText("""
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

Now your Kotlin code can use `${blockClass.simpleName}<T>` as a source enriched replacement for `() -> T`.
You can call `${CapturedBlock<*>::source.name}()` on any instance of
`${blockClass.simpleName}` to access the source text for that block.

```kotlin
$importStatement

${
    execSource {
        val captured = CapturedBlock {
            println("Hello!")
        }
            
        check(captured.source() == """println("Hello!")""")
    }
}
```

You can invoke the block similar to a regular function: 

```kotlin
$importStatement

${
    execSource {
        fun equation(block: CapturedBlock<Int>): String {
            val result = block() // invoke the block
            
            return "${block.source()} = $result"
        }
        
        check(equation { 2 + 2 } == "2 + 2 = 4")
    }
}
```

## Purpose

The purpose of this plugin is to support experimental literate
programming and documentation generation techniques in Kotlin.

An example of this is the code used to generate this README.md.
Capturing source from blocks allows sample code to be run and
tested during generation.

View the source here: [readme/src/main/kotlin/Main.kt](readme/src/main/kotlin/Main.kt)
    """.trim())
}