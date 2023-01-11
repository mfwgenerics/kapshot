import io.koalaql.markout.md.markdownString
import org.junit.jupiter.api.Test

class MainTest {
    @Test
    fun `smoke test generated markdown`() {
        /* checks are run in the sample code */
        markdownString { generateMarkdown() }
    }
}