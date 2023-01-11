import io.koalaql.kapshot.*
import io.koalaql.markout.markout
import kotlin.io.path.Path
import kotlin.io.path.writeText

class FakePrintln {
    val printed = StringBuilder()

    fun println(out: String) {
        printed.append(out)
        printed.append("\n")
    }

    override fun toString(): String = "$printed"
}

fun interface PrintingBlock: Capturable<PrintingBlock> {
    operator fun FakePrintln.invoke()

    override fun withSource(source: Source): PrintingBlock = object : PrintingBlock by this {
        override val source: Source = source
    }
}

fun execSource(block: CapturedBlock<*>): String {
    block()
    return block.source.text
}

fun printSource(println: FakePrintln, block: PrintingBlock): String {
    with (block) { println.invoke() }

    return block.source.text
}

@CaptureSource
/* must be a fun interface to support SAM conversion from blocks */
fun interface CustomCapturable<T, R> : Capturable<CustomCapturable<T, R>> {
    /* invoke is not special. this could be any single abstract method */
    operator fun invoke(arg: T): R

    /* withSource is called by the plugin to add source information */
    override fun withSource(source: Source): CustomCapturable<T, R> =
        object : CustomCapturable<T, R> by this { override val source = source }
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

    id("io.koalaql.kapshot-plugin") version "0.1.0"
}
```

### Capturing Blocks

Now your Kotlin code can use `${blockClass.simpleName}<T>` as a source enriched replacement for `() -> T`.
You can use the `${CapturedBlock<*>::source.name}` property on any instance of
`${blockClass.simpleName}` to access the source for that block.

```kotlin
$importStatement

${
    execSource {
        val captured = CapturedBlock {
            println("Hello!")
        }

        check(captured.source.text == """println("Hello!")""")
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

            return "${block.source} = $result"
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
            return "$this.map { ${block.source} } = ${map { block(it) }}"
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
            sourceOf<MyClass>().text ==
            """
            class MyClass {
                @CaptureSource
                fun twelve() = 12
            }
            """.trimIndent()
        )
        
        check(
            sourceOf(MyClass::twelve).text ==
            "fun twelve() = 12"
        )
    }
}
```

### Source Location
 
The `${Source::class.simpleName}::${Source::location.name}` property
contains information about the location of captured source code
including the file path (relative to the project root directory)
and the char, line and column offsets for both the start
and end of the captured source. Offsets are 0-indexed.

${run {
        
val println = FakePrintln()
    
val source = printSource(println) {
    val source = CapturedBlock { 2 + 2 }.source
    val location = source.location
    
    println(
        "`${source.text}`"
        + " found in ${location.path}"
        + " @ line ${location.from.line+1}"
    )
}
        
"""

```kotlin
$source
```

The code above will print the following:

```
${println.toString().trim()}
```

"""
}}

## Purpose

The purpose of this plugin is to support experimental literate
programming and documentation generation techniques in Kotlin.

An example of this is the code used to generate this README.md.
Capturing source from blocks allows sample code to be run and
tested during generation.

View the source here: [readme/src/main/kotlin/Main.kt](readme/src/main/kotlin/Main.kt)

    """.trim()
}

fun main() = markout(Path("..")) {
    file("README.md", generateMarkdown())
}