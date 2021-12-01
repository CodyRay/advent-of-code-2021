import org.junit.Assert.assertEquals
import org.junit.Test

val EXAMPLE_INPUT = listOf(199, 200, 208, 210, 200, 207, 240, 269, 260, 263)

fun parseDepths(lines: List<String>): List<Int> = lines.map { it.toInt() }

fun List<Int>.windowedIncreasing(size: Int): Int = windowed(size).map { it.sum() }.windowed(2).count { (x, y) -> x < y }

class Day01 {
    @Test
    fun main() {
        val depths = parseDepths(readPuzzleInputLines("Day01"))
        println("Day 1, Part 1: ${depths.windowedIncreasing(1)}")
        println("Day 1, Part 2: ${depths.windowedIncreasing(3)}")
    }

    @Test
    fun `test depth increasing`() {
        assertEquals(7, EXAMPLE_INPUT.windowedIncreasing(1))
    }

    @Test
    fun `test depth increasing one number`() {
        assertEquals(0, listOf(199).windowedIncreasing(1))
    }

    @Test
    fun `test depth increasing two numbers increasing`() {
        assertEquals(1, listOf(199, 200).windowedIncreasing(1))
    }

    @Test
    fun `test depth increasing two numbers decreasing`() {
        assertEquals(0, listOf(199, 180).windowedIncreasing(1))
    }

    @Test
    fun `test parsing`() {
        assertEquals(listOf(199, 200), parseDepths(listOf("199", "200")))
    }

    @Test
    fun `test part1`() {
        assertEquals(1167, parseDepths(readPuzzleInputLines("Day01")).windowedIncreasing(1))
    }

    @Test
    fun `test windowed depth increasing`() {
        assertEquals(5, EXAMPLE_INPUT.windowedIncreasing(3))
    }

    @Test
    fun `test part2`() {
        assertEquals(1130, parseDepths(readPuzzleInputLines("Day01")).windowedIncreasing(3))
    }
}