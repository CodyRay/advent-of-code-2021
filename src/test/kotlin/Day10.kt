@file:Suppress("PackageDirectoryMismatch")

package day10

import org.junit.Test
import readPuzzleInputLines
import kotlin.test.assertEquals

fun List<Long>.middle() = get(size / 2)

val brackets = mapOf('(' to ')', '[' to ']', '{' to '}', '<' to '>')
val errorScores = mapOf(')' to 3, ']' to 57, '}' to 1197, '>' to 25137)
val autoCompleteScores = mapOf(')' to 1L, ']' to 2L, '}' to 3L, '>' to 4L)

fun autocompleteStr(s: String): String? {
    val queue = mutableListOf<Char>()
    for (c in s) {
        if (c in brackets) {
            queue.add(brackets[c]!!)
        } else if (queue.isNotEmpty() && c == queue.last()) {
            queue.removeLast()
        } else {
            return null
        }
    }
    return queue.reversed().toCharArray().concatToString()
}

fun checkStr(s: String): Char? {
    val queue = mutableListOf<Char>()
    for (c in s) {
        if (c in brackets) {
            queue.add(brackets[c]!!)
        } else if (queue.isNotEmpty() && c == queue.last()) {
            queue.removeLast()
        } else {
            return c
        }
    }
    return null
}


fun countInvalidChar(example: List<String>) =
    example.mapNotNull { checkStr(it) }.groupBy { it }.mapValues { it.value.size }

fun errorScore(occurrences: Map<Char, Int>): Int {
    return occurrences.keys.sumOf { occurrences[it]!! * errorScores[it]!! }
}

fun mapAutoCompleteScores(example: List<String>) =
    example.mapNotNull { autocompleteStr(it) }.map { it.fold(0L) { acc, x -> acc * 5L + autoCompleteScores[x]!! } }

class Day10 {
    @Test
    fun main() {
        val parsed = readPuzzleInputLines("Day10")
        val part1 = errorScore(countInvalidChar(parsed))
        println("Day 10, Part 1: $part1")
        assertEquals(319233, part1)
        val part2 = mapAutoCompleteScores(parsed).sorted().middle()
        println("Day 10, Part 2: $part2")
        assertEquals(1118976874, part2)
    }

    @Test
    fun `test solution part1`() {
        val example = listOf(
            "[({(<(())[]>[[{[]{<()<>>",
            "[(()[<>])]({[<{<<[]>>(",
            "{([(<{}[<>[]}>{[]{[(<()>",
            "(((({<>}<{<{<>}{[]{[]{}",
            "[[<[([]))<([[{}[[()]]]",
            "[{[{({}]{}}([{[{{{}}([]",
            "{<[[]]>}<{[{[{[]{()[[[]",
            "[<(<(<(<{}))><([]([]()",
            "<{([([[(<>()){}]>(<<{{",
            "<{([{{}}[<[[[<>{}]]]>[]]",
        )
        assertEquals(5, example.count { checkStr(it) !== null })
        assertEquals(mapOf('}' to 1, ')' to 2, ']' to 1, '>' to 1), countInvalidChar(example))
        assertEquals(26397, errorScore(countInvalidChar(example)))

        assertEquals(5, example.count { autocompleteStr(it) !== null })
        assertEquals(26397, errorScore(countInvalidChar(example)))
        assertEquals(listOf(294L, 5566, 288957, 995444, 1480781), mapAutoCompleteScores(example).sorted())
        assertEquals(288957, mapAutoCompleteScores(example).sorted().middle())
    }
}