import io.koalaql.kapshot.CaptureSource
import io.koalaql.kapshot.sourceOf
import kotlin.test.Test

class CaptureSourceTests {
    @CaptureSource
    interface TestClass {
        fun boo() = "boo!"
    }

    @Test
    fun `capture class source`() {
        sourceOf<TestClass>()
    }

    @CaptureSource
    @Test
    fun `capture method source`() {
        sourceOf(::`capture method source`)
    }
}