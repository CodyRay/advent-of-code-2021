import org.junit.Test

import org.junit.Assert.*

class Day00KtTest {
    @Test
    fun `test ready`() {
        assertEquals(readyToAdventOfCode(readPuzzleInputLines("day00").first()), true)
    }
}