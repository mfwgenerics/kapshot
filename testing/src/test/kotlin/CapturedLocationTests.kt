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
        assertEquals(210, location.from.char)
        assertEquals(13, location.from.column)
        assertEquals(9, location.from.line)
    }
}