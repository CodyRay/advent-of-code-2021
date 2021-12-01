import org.junit.Assert.assertEquals
import org.junit.Test

val EXAMPLE_INPUT = listOf(199, 200, 208, 210, 200, 207, 240, 269, 260, 263)

class Day01KtTest {
    @Test
    fun `test depth increasing`() {
        assertEquals(7, depthIncreasing(EXAMPLE_INPUT))
    }

    @Test
    fun `test depth increasing one number`() {
        assertEquals(0, depthIncreasing(listOf(199)))
    }

    @Test
    fun `test depth increasing two numbers increasing`() {
        assertEquals(1, depthIncreasing(listOf(199, 200)))
    }

    @Test
    fun `test depth increasing two numbers decreasing`() {
        assertEquals(0, depthIncreasing(listOf(199, 180)))
    }

    @Test
    fun `test parsing`() {
        assertEquals(listOf(199, 200), parseDepths(listOf("199", "200")))
    }

    @Test
    fun `test part1`() {
        assertEquals(1167, depthIncreasing(parseDepths(readPuzzleInputLines("day01"))))
    }
}