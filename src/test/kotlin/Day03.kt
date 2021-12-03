import org.junit.Test
import kotlin.test.assertEquals


fun part1(digits: List<String>): Pair<Int, Int> {
    val totalDigits = digits.size
    val counterArray = Array(digits.first().length) { 0 }
    digits.forEach {
        it.forEachIndexed { x, c -> counterArray[x] += c.digitToInt() }
    }
    val gamma = counterArray.map { if (2 * it > totalDigits) '1' else '0' }.toCharArray().concatToString().toInt(2)
    val epsilon = counterArray.map { if (2 * it > totalDigits) '0' else '1' }.toCharArray().concatToString().toInt(2)
    return Pair(gamma, epsilon)
}

fun part2(digits: List<String>): Pair<Int, Int> {
    val oxygenGeneratorRating =
        digits.first().indices.fold(digits) { acc, i -> bitCriteriaPartition(acc, i).first }.single().toInt(2)
    val co2ScrubberRating =
        digits.first().indices.fold(digits) { acc, i -> bitCriteriaPartition(acc, i).second }.single().toInt(2)
    return Pair(oxygenGeneratorRating, co2ScrubberRating)
}

fun bitCriteriaPartition(digits: List<String>, index: Int): Pair<List<String>, List<String>> {
    val (ones, zeros) = digits.partition { it[index] == '1' }
    if (ones.size + zeros.size == 1) {
        return Pair(ones + zeros, ones + zeros)
    }
    return if (ones.size >= zeros.size) Pair(ones, zeros) else Pair(zeros, ones)
}

val EXAMPLE_DATA_03 = listOf(
    "00100",
    "11110",
    "10110",
    "10111",
    "10101",
    "01111",
    "00111",
    "11100",
    "10000",
    "11001",
    "00010",
    "01010",
)

class Day03 {
    @Test
    fun main() {
        val parsed = readPuzzleInputLines("Day03")
        val part1 = part1(parsed).let { it.first * it.second }
        println("Day 3, Part 1: $part1")
        assertEquals(2261546, part1)
        val part2 = part2(parsed).let { it.first * it.second }
        println("Day 3, Part 2: $part2")
        assertEquals(6775520, part2)
    }

    @Test
    fun `test parsing`() {
    }

    @Test
    fun `test part1`() {
        val (exampleGamma, exampleEpsilon) = part1(EXAMPLE_DATA_03)
        assertEquals(22, exampleGamma)
        assertEquals(9, exampleEpsilon)
    }

    @Test
    fun `test part2`() {
        val (oxygenGeneratorRating, co2ScrubberRating) = part2(EXAMPLE_DATA_03)
        assertEquals(23, oxygenGeneratorRating)
        assertEquals(10, co2ScrubberRating)
    }
}