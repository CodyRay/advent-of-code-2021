import org.junit.Assert.assertEquals
import org.junit.Test

fun readyToAdventOfCode(input: String): Boolean {
    return input.toBooleanStrict()
}

class Day00 {
    @Test
    fun `test ready`() {
        assertEquals(readyToAdventOfCode(readPuzzleInputLines("Day00").first()), true)
    }
}