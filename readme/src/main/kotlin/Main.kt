import io.koalaql.kapshot.Capturable
import io.koalaql.kapshot.CaptureSource
import io.koalaql.kapshot.CapturedBlock
import io.koalaql.kapshot.sourceOf
import kotlin.io.path.Path
import kotlin.io.path.writeText

fun execSource(block: CapturedBlock<*>): String {
    block()
    return block.source()
}

@CaptureSource
/* must be a fun interface to support SAM conversion from blocks */
fun interface CustomCapturable<T, R> : Capturable<CustomCapturable<T, R>> {
    /* invoke is not special. this could be any single abstract method */
    operator fun invoke(arg: T): R

    /* withSource is called by the plugin to add source information */
    override fun withSource(source: String): CustomCapturable<T, R> =
        object : CustomCapturable<T, R> by this { override fun source(): String = source }
}

fun generateMarkdown(): String {
    val capturableClass = Capturable::class
    val blockClass = CapturedBlock::class
    val captureAnno = CaptureSource::class

    val importStatement = "import ${blockClass.qualifiedName}"

    return """
# Kapshot
Kapshot is a simple Kotlin compiler plugin for capturing source code text from closure blocks and declarations.

## Usage

Include the `io.koalaql.kapshot-plugin` Gradle plugin in your `build.gradle.kts`:

```kotlin
plugins {
    /* ... */

    id("io.koalaql.kapshot-plugin") version "0.0.2"
}
```

### Capturing Blocks

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

### Parameterized Blocks

The default `${blockClass.simpleName}` interface doesn't accept any
arguments to `invoke` and is only generic on the return type. This
means the captured source block must depend only on state from the
enclosing scope. To write source capturing versions of builder blocks
or common higher-order functions like `map` and `filter` you will
need to define your own capture interface that extends `${capturableClass.simpleName}`.

```kotlin
${sourceOf<CustomCapturable<*, *>>()}
```

Once you have declared your own `${capturableClass.simpleName}` you can use it
in a similar way to `${blockClass.simpleName}` from above.

```kotlin
${
    execSource {
        fun <T> List<T>.mapped(block: CustomCapturable<T, T>): String {
            return "$this.map { ${block.source()} } = ${map { block(it) }}"
        }

        check(
            listOf(1, 2, 3).mapped { x -> x*2 } ==
            "[1, 2, 3].map { x -> x*2 } = [2, 4, 6]"
        )
    }
}
```

If it is present, the block's argument list is considered part of its source text.
 
### Declarations

You can capture declaration sources using the `@${captureAnno.simpleName}`
annotation. The source of annotated declarations can then be retrieved using
`sourceOf<T>` for class declarations or `sourceOf(::method)` for method
declarations. The source capture starts at the end of the `@${captureAnno.simpleName}`
annotation.

```kotlin
${
    execSource {
        @CaptureSource
        class MyClass {
            @CaptureSource
            fun twelve() = 12
        }
        
        check(
            sourceOf<MyClass>() ==
            """
            class MyClass {
                @CaptureSource
                fun twelve() = 12
            }
            """.trimIndent()
        )
        
        check(
            sourceOf(MyClass::twelve) ==
            "fun twelve() = 12"
        )
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

    """.trim()
}

fun main() {
    Path("../README.md").writeText(generateMarkdown())
}