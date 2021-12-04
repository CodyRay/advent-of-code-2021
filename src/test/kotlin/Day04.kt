@file:Suppress("PackageDirectoryMismatch")

package day04

import org.junit.Test
import readPuzzleInputText
import kotlin.test.assertEquals
import kotlin.test.assertTrue

fun <T> List<T>.splitFirst() = Pair(take(1).single(), drop(1))

val EXAMPLE_TXT = """
    7,4,9,5,11,17,23,2,0,14,21,24,10,16,13,6,15,25,12,22,18,20,8,19,3,26,1
    
    22 13 17 11  0
     8  2 23  4 24
    21  9 14 16  7
     6 10  3 18  5
     1 12 20 15 19
    
     3 15  0  2 22
     9 18 13 17  5
    19  8  7 25 23
    20 11 10 24  4
    14 21 16 12  6
    
    14 21 17 24  4
    10 16 15  9 19
    18  8 23 26 20
    22 11 13  6  5
     2  0 12  3  7
""".trimIndent()

fun parseBingo(exampleTxt: String): Pair<List<Int>, List<BingoBoard>> {
    val (callsStr, boardsStr) = exampleTxt.split("\n\n").splitFirst()
    val calls = callsStr.split(',').map { it.toInt() }
    val boards = boardsStr.map { boardTxt ->
        BingoBoard(boardTxt.split("""\s+""".toRegex()).filter { !it.isBlank() }.map { it.toInt() })
    }
    return Pair(calls, boards)
}

val BINGO_PATTERNS = listOf(
    // listOf((0..4).map { x -> x * 6 }, (0..4).map { x -> x * 4 + 4 }),
    (0..4).map { x -> (0..4).map { y -> x * 5 + y } },
    (0..4).map { x -> (0..4).map { y -> x + y * 5 } }
).flatten()


data class BingoBoard(val boardValues: List<Int>) {
    fun isBingo(calls: Set<Int>): Boolean = BINGO_PATTERNS.any { pattern -> pattern.all { boardValues[it] in calls } }
    fun getBingoPattern(calls: Set<Int>): List<Int> =
        BINGO_PATTERNS.first { pattern -> pattern.all { boardValues[it] in calls } }

    fun unmarked(calls: Set<Int>): List<Int> = boardValues.filter { it !in calls }
}

fun part1(calls: List<Int>, boards: List<BingoBoard>): Int {
    val (winningCalls, winningBoards) = playBingo(calls, boards)
        .first { (_, bs) -> bs.any() }
    return winningBoards.single().unmarked(winningCalls.toSet()).sum() * winningCalls.last()
}

fun part2(calls: List<Int>, boards: List<BingoBoard>): Int {
    val (pen, final) = playBingo(calls, boards)
        .zipWithNext()
        .takeWhile { (lastState, _) -> lastState.third.any() }
        .map { it.second }
        .toList()
        .takeLast(2)

    val (_, penBoards) = pen
    val (finalCalls, finalBoards) = final
    val lastBoard = finalBoards.subtract(penBoards.toSet()).single()
    return lastBoard.unmarked(finalCalls.toSet()).sum() * finalCalls.last()
}

private fun playBingo(calls: List<Int>, boards: List<BingoBoard>) =
    calls.indices
        .asSequence()
        .map { calls.take(it) }
        .map { partialCalls ->
            val partialCallsSet = partialCalls.toSet()
            val (bingo, noBingo) = boards.partition { it.isBingo(partialCallsSet) }
            Triple(partialCalls, bingo, noBingo)
        }

class Day04 {
    @Test
    fun main() {
        val (calls, boards) = parseBingo(readPuzzleInputText("Day04"))
        val part1 = part1(calls, boards)
        println("Day 4, Part 1: $part1")
        assertEquals(5685, part1)
        val part2 = part2(calls, boards)
        println("Day 4, Part 2: $part2")
        assertEquals(21070, part2)
    }

    @Test
    fun `test parsing`() {
        val (calls, boards) = parseBingo(EXAMPLE_TXT)
        assertEquals(
            listOf(
                7, 4, 9, 5, 11, 17, 23, 2, 0, 14, 21, 24, 10, 16, 13, 6, 15, 25, 12, 22, 18, 20, 8, 19, 3, 26, 1
            ), calls
        )
        assertEquals(
            boards.first(), BingoBoard(
                listOf(
                    22, 13, 17, 11, 0,
                    8, 2, 23, 4, 24,
                    21, 9, 14, 16, 7,
                    6, 10, 3, 18, 5,
                    1, 12, 20, 15, 19
                )
            )
        )
    }

    @Test
    fun `test bingo`() {
        val (_, boards) = parseBingo(EXAMPLE_TXT)
        val board = boards.first()
        assertTrue(board.isBingo(setOf(22, 13, 17, 11, 0)))
        // assertTrue(board.isBingo(setOf(22, 2, 14, 18, 19)))
        assertTrue(board.isBingo(setOf(0, 24, 7, 5, 19)))
        assertEquals(
            listOf(
                8, 2, 23, 4, 24,
                21, 9, 14, 16, 7,
                6, 10, 3, 18, 5,
                1, 12, 20, 15, 19
            ).sum(), board.unmarked(setOf(22, 13, 17, 11, 0)).sum()
        )
    }


    @Test
    fun `test solution`() {
        val (calls, boards) = parseBingo(EXAMPLE_TXT)
        assertEquals(4512, part1(calls, boards))
        assertEquals(1924, part2(calls, boards))
    }
}