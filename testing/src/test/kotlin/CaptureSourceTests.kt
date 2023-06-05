import io.koalaql.kapshot.CaptureSource
import io.koalaql.kapshot.sourceOf
import kotlin.test.Test
import kotlin.test.assertEquals

class CaptureSourceTests {
    @CaptureSource
    interface TestClass {
        fun boo() = "boo!"

        @CaptureSource
        class Nested {
            val test get() = "nest"
        }
    }

    @Test
    fun `capture class source`() {
        val source = sourceOf<TestClass>()

        assertEquals(
            """
            interface TestClass {
                fun boo() = "boo!"
        
                @CaptureSource
                class Nested {
                    val test get() = "nest"
                }
            }
            """.trimIndent(),
            source.text
        )

        val location = source.location

        assertEquals("src/test/kotlin/CaptureSourceTests.kt", location.path)

        assertEquals(182, location.from.char)
        assertEquals(7, location.from.line)
        assertEquals(4, location.from.column)

        assertEquals(329, location.to.char)
        assertEquals(14, location.to.line)
        assertEquals(5, location.to.column)
    }

    @Test
    fun `capture nested class source`() {
        assertEquals(
            """
            class Nested {
                val test get() = "nest"
            }
            """.trimIndent(),
            sourceOf<TestClass.Nested>().text
        )
    }

    private val capMethodSourceSource = """
    @Test
    fun `capture method source`() {
        class Inner {
            @CaptureSource
            fun five() = 5
        }

        assertEquals(
            capMethodSourceSource,
            sourceOf(::`capture method source`).text
        )

        assertEquals(
            "fun five() = 5", sourceOf(Inner::five).text
        )
    }
    """.trimIndent()

    @CaptureSource
    @Test
    fun `capture method source`() {
        class Inner {
            @CaptureSource
            fun five() = 5
        }

        assertEquals(
            capMethodSourceSource,
            sourceOf(::`capture method source`).text
        )

        assertEquals(
            "fun five() = 5", sourceOf(Inner::five).text
        )
    }

    @CaptureSource
    interface CapturedInterface {
        fun type() = "interface"
    }

    @CaptureSource
    object CapturedObj {
        val test = "123"
    }

    @CaptureSource
    sealed class CapturedSealed {
        object Left: CapturedSealed()
        object Right: CapturedSealed()

        @CaptureSource
        companion object { }
    }

    @Test
    fun `capture assorted declarations`() {
        assertEquals(
            """
            interface CapturedInterface {
                fun type() = "interface"
            }
            """.trimIndent(),
            sourceOf<CapturedInterface>().text
        )

        assertEquals(
            """
            object CapturedObj {
                val test = "123"
            }
            """.trimIndent(),
            sourceOf<CapturedObj>().text
        )

        assertEquals(
            """
            sealed class CapturedSealed {
                object Left: CapturedSealed()
                object Right: CapturedSealed()

                @CaptureSource
                companion object { }
            }
            """.trimIndent(),
            sourceOf<CapturedSealed>().text
        )

        assertEquals(
            """
                companion object { }
            """.trimIndent(),
            sourceOf<CapturedSealed.Companion>().text
        )
    }

    @CaptureSource
    @Transient /* test this annotation is present in source */
    private val someVal = 5

    @CaptureSource
    private var someVar: Int get() = 10
        set(value) { }

    @Test
    fun `can capture source of valvars`() {
        assertEquals(
            """
                @Transient /* test this annotation is present in source */
                private val someVal = 5
            """.trimIndent(),
            sourceOf(::someVal).text
        )

        assertEquals(
            """
                private var someVar: Int get() = 10
                    set(value) { }
            """.trimIndent(),
            sourceOf(::someVar).text
        )
    }
}