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
        assertEquals(
            capMethodSourceSource,
            sourceOf(::`capture method source`)
        )
    }
    """.trimIndent()

    @CaptureSource
    @Test
    fun `capture method source`() {
        assertEquals(
            capMethodSourceSource,
            sourceOf(::`capture method source`)
        )
    }
}