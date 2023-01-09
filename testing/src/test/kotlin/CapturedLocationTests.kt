import io.koalaql.kapshot.CapturedBlock
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class CapturedLocationTests {
    @Test
    fun `location case`() {
        val block = CapturedBlock {
            2 + 2
        }

        val location = block.source.location

        assertEquals("2 + 2", block.source.text)
        assertEquals("src/test/kotlin/CapturedLocationTests.kt", location.path)

        assertEquals(223, location.from.char)
        assertEquals(12, location.from.column)
        assertEquals(8, location.from.line)

        assertEquals(228, location.to.char)
        assertEquals(17, location.to.column)
        assertEquals(8, location.to.line)
    }

    @Test
    fun `empty block`() {
        val block = CapturedBlock {

        }

        val location = block.source.location

        assertEquals("", block.source.text)
        assertEquals("src/test/kotlin/CapturedLocationTests.kt", location.path)

        assertEquals(763, location.from.char)
        assertEquals(35, location.from.column)
        assertEquals(27, location.from.line)

        assertEquals(763, location.to.char)
        assertEquals(35, location.to.column)
        assertEquals(27, location.to.line)
    }

    @Test
    fun `really empty block`() {
        val block = CapturedBlock {}

        val location = block.source.location

        assertEquals("", block.source.text)
        assertEquals("src/test/kotlin/CapturedLocationTests.kt", location.path)

        assertEquals(1303, location.from.char)
        assertEquals(35, location.from.column)
        assertEquals(47, location.from.line)

        assertEquals(1303, location.to.char)
        assertEquals(35, location.to.column)
        assertEquals(47, location.to.line)
    }
}