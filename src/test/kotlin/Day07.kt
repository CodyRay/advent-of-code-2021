@file:Suppress("PackageDirectoryMismatch")

package day07

import org.junit.Test
import readPuzzleInputText
import toIntsFromCommaSeparated
import kotlin.math.abs
import kotlin.test.assertEquals

fun nthTriangular(n: Int): Int = (n downTo 1).sumOf { it }
fun getPossiblePositions(positions: List<Int>) = positions.minOf { it }..positions.maxOf { it }
fun linearFuelCost(startingPositions: List<Int>, position: Int) = startingPositions.sumOf { abs(it - position) }
fun triangularFuelCost(startingPositions: List<Int>, position: Int) =
    startingPositions.sumOf { nthTriangular(abs(it - position)) }

class Day07 {
    @Test
    fun main() {
        val parsed = readPuzzleInputText("Day07").toIntsFromCommaSeparated()
        val part1 = getPossiblePositions(parsed).minOf { linearFuelCost(parsed, it) }
        println("Day 7, Part 1: $part1")
        assertEquals(342641, part1)
        val part2 = getPossiblePositions(parsed).minOf { triangularFuelCost(parsed, it) }
        println("Day 7, Part 2: $part2")
        assertEquals(93006301, part2)
    }

    @Test
    fun `test solution`() {
        val example = listOf(16, 1, 2, 0, 4, 2, 7, 1, 2, 14)
        assertEquals(37, getPossiblePositions(example).minOf { linearFuelCost(example, it) })
        assertEquals(0, nthTriangular(0))
        assertEquals(1, nthTriangular(1))
        assertEquals(3, nthTriangular(2))
        assertEquals(6, nthTriangular(3))
        assertEquals(168, getPossiblePositions(example).minOf { triangularFuelCost(example, it) })
    }
}