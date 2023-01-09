import io.koalaql.kapshot.CapturedBlock
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class CapturedLocationTests {
    @Test
    fun `location case`() {
        val block = CapturedBlock {
            2 + 2
        }

        val location = block.source().location

        assertEquals("2 + 2", block.source().text)
        assertEquals("src/test/kotlin/CapturedLocationTests.kt", location.path)
        assertEquals(223, location.from.char)
        assertEquals(12, location.from.column)
        assertEquals(8, location.from.line)
    }
}