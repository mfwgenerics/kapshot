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
            sourceOf<TestClass>()
        )
    }

    @Test
    fun `capture nested class source`() {
        assertEquals(
            """
            class Nested {
                val test get() = "nest"
            }
            """.trimIndent(),
            sourceOf<TestClass.Nested>()
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
            sourceOf(::`capture method source`)
        )

        assertEquals(
            "fun five() = 5", sourceOf(Inner::five)
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
            sourceOf(::`capture method source`)
        )

        assertEquals(
            "fun five() = 5", sourceOf(Inner::five)
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
            sourceOf<CapturedInterface>()
        )

        assertEquals(
            """
            object CapturedObj {
                val test = "123"
            }
            """.trimIndent(),
            sourceOf<CapturedObj>()
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
            sourceOf<CapturedSealed>()
        )

        assertEquals(
            """
                companion object { }
            """.trimIndent(),
            sourceOf<CapturedSealed.Companion>()
        )
    }
}